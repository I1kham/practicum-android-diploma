package ru.practicum.android.diploma.presentation.industry

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.filter.FilterInteractor
import ru.practicum.android.diploma.domain.api.industries.IndustriesInteractor
import ru.practicum.android.diploma.domain.models.entity.FilterShared
import ru.practicum.android.diploma.domain.models.entity.IndustryNested
import ru.practicum.android.diploma.ui.industry.models.IndustryFragmentState
import ru.practicum.android.diploma.util.Resource
import ru.practicum.android.diploma.util.ResponseStatusCode
import ru.practicum.android.diploma.util.debounce

class IndustryViewModel(
    private val interactor: IndustriesInteractor,
    private val filterInteractor: FilterInteractor
) : ViewModel() {
    private var unFilteredList: List<IndustryNested> = emptyList()
    private var filterShared: FilterShared? = null
    private val _industriesState = MutableLiveData<IndustryFragmentState>()
    val industries: LiveData<IndustryFragmentState> = _industriesState
    private var selectedIndustry: IndustryNested? = null
    val filterDebounce = debounce<String>(
        delayMillis = DEBOUNCE_FILTER_TIME,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { searchText ->
        filterIndustries(searchText.trim())
    }

    init {
        getFilter()
        getIndustries()
    }

    fun saveFilter() {
        viewModelScope.launch {
            filterInteractor.saveFilter(
                FilterShared(
                    countryId = filterShared?.countryId,
                    countryName = filterShared?.countryName,
                    regionId = filterShared?.regionId,
                    regionName = filterShared?.regionName,
                    industryName = selectedIndustry?.name,
                    industryId = selectedIndustry?.id,
                    salary = filterShared?.salary,
                    onlySalaryFlag = filterShared?.onlySalaryFlag,
                    apply = null
                )
            )
        }
        pushState(IndustryFragmentState.Exit)
    }

    private fun getFilter() {
        viewModelScope.launch {
            filterShared = filterInteractor.getFilter()
            selectedIndustry = IndustryNested(
                filterShared?.industryId,
                filterShared?.industryName
            )
        }
    }

    private fun getIndustries() {
        viewModelScope.launch {
            interactor.getIndustriesList()
                .onStart { pushState(IndustryFragmentState.Loading) }
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            unFilteredList = result.data?.flatMap { industry ->
                                industry.industries ?: emptyList()
                            } ?: emptyList()
                            if (unFilteredList.isEmpty()) {
                                pushState(IndustryFragmentState.Empty)
                            } else {
                                pushState(IndustryFragmentState.Content(unFilteredList, selectedIndustry))
                            }
                        }

                        is Resource.Error -> {
                            if (result.responseCode == ResponseStatusCode.NoInternet) {
                                pushState(IndustryFragmentState.NoInternet)
                            } else {
                                pushState(IndustryFragmentState.Error)
                            }
                        }
                    }
                }
        }
    }

    fun setSelectedIndustry(industry: IndustryNested?, isChecked: Boolean) {
        selectedIndustry = if (isChecked) {
            industry
        } else {
            null
        }
        Log.d("setSelectedIndustry :", industry.toString())
    }

    private fun filterIndustries(query: String) {
        viewModelScope.launch {
            val filteredList = if (query.isEmpty()) {
                unFilteredList
            } else {
                unFilteredList.filter { industry ->
                    industry.name?.contains(query, ignoreCase = true) == true
                }
            }
            if (filteredList.isEmpty()) {
                pushState(IndustryFragmentState.Empty)
            } else {
                pushState(IndustryFragmentState.Content(filteredList, selectedIndustry))
            }
        }
    }

    private fun pushState(state: IndustryFragmentState) {
        _industriesState.value = state
    }

    private companion object {
        const val DEBOUNCE_FILTER_TIME = 1000L
    }
}
