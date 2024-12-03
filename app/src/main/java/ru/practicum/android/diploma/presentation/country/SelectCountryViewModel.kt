package ru.practicum.android.diploma.presentation.country

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.filter.FilterInteractor
import ru.practicum.android.diploma.domain.api.hh.HhInteractor
import ru.practicum.android.diploma.domain.models.entity.Area
import ru.practicum.android.diploma.domain.models.entity.FilterShared
import ru.practicum.android.diploma.ui.country.model.CountrySelectState
import ru.practicum.android.diploma.ui.root.RootActivity.Companion.NOT_DESIRED_AREA_KEY
import ru.practicum.android.diploma.util.Resource
import ru.practicum.android.diploma.util.ResponseStatusCode
import java.net.SocketTimeoutException

class SelectCountryViewModel(
    private val hhInteractor: HhInteractor,
    private val filterInteractor: FilterInteractor
) :
    ViewModel() {
    private val _countrySelectState = MutableLiveData<CountrySelectState>()
    val countrySelectState: LiveData<CountrySelectState> = _countrySelectState
    private var countriesList: MutableList<Area> = mutableListOf()
    private var filterShared: FilterShared? = null

    init {
        getCountries()
        viewModelScope.launch {
            filterShared = filterInteractor.getTempFilter()
        }
    }

    fun chooseCountry() = { country: Area ->
        saveToFilter(country)
        renderState(CountrySelectState.Exit)
    }

    private fun saveToFilter(country: Area) {
        viewModelScope.launch {
            filterInteractor.saveTempFilter(
                FilterShared(
                    countryId = country.id,
                    countryName = country.name,
                    regionId = null,
                    regionName = null,
                    industryName = filterShared?.industryName,
                    industryId = filterShared?.industryId,
                    salary = filterShared?.salary,
                    onlySalaryFlag = filterShared?.onlySalaryFlag,
                    apply = null
                )
            )
        }
    }

    private fun getCountries() {
        viewModelScope.launch {
            renderState(CountrySelectState.Loading)
            try {
                hhInteractor.searchCountries().collect { resource ->
                    when (resource) {
                        is Resource.Error -> {
                            if (resource.responseCode == ResponseStatusCode.NoInternet) {
                                renderState(CountrySelectState.NoInternet)
                            } else {
                                renderState(CountrySelectState.Error)
                            }
                        }

                        is Resource.Success -> {
                            if (resource.data.isNullOrEmpty()) {
                                renderState(CountrySelectState.Empty)
                            } else {
                                val sortedCountryList = resource.data.sortedBy { it.id == NOT_DESIRED_AREA_KEY }
                                countriesList.addAll(sortedCountryList)
                                renderState(CountrySelectState.Success(sortedCountryList))
                            }
                        }
                    }
                }
            } catch (e: SocketTimeoutException) {
                Log.e("SocketTimeoutException", "Timeout error occured", e)
                renderState(CountrySelectState.Error)
                this.coroutineContext.job.cancel()
            }
        }
    }

    private fun renderState(state: CountrySelectState) {
        _countrySelectState.postValue(state)
    }
}
