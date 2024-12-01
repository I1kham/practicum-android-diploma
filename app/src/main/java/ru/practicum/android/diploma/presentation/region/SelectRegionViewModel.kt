package ru.practicum.android.diploma.presentation.region

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.filter.FilterInteractor
import ru.practicum.android.diploma.domain.models.entity.FilterShared
import ru.practicum.android.diploma.ui.region.model.SelectRegionFragmentState

class SelectRegionViewModel(
    private val filterInteractor: FilterInteractor
) : ViewModel() {
    private var firstStart = true
    private val _state = MutableLiveData<SelectRegionFragmentState>()
    val state: LiveData<SelectRegionFragmentState> = _state
    private var filterShared: FilterShared? = null
        set(value) {
            pushState(
                SelectRegionFragmentState.Content(
                    value?.countryName,
                    value?.regionName,
                    value?.countryId
                )
            )
            field = value
        }

    fun getFilter() {
        viewModelScope.launch {
            if (firstStart) {
                firstStart = false
                filterShared = filterInteractor.getFilter()
                filterInteractor.saveTempFilter(filterShared)
            } else {
                firstStart = false
                filterShared = filterInteractor.getTempFilter()
            }
        }
    }

    fun clearCountry() {
        filterShared = filterShared?.copy(
            countryName = null,
            countryId = null,
        )
        viewModelScope.launch {
            filterInteractor.saveTempFilter(filterShared)
        }
    }

    fun clearArea() {
        filterShared = filterShared?.copy(
            regionId = null,
            regionName = null,
        )
        viewModelScope.launch {
            filterInteractor.saveTempFilter(filterShared)
        }
    }

    fun saveExit() {
        viewModelScope.launch {
            filterInteractor.saveTempFilter(null)
            filterInteractor.saveFilter(filterShared)
            pushState(SelectRegionFragmentState.Exit)
        }
    }

    private fun pushState(state: SelectRegionFragmentState) {
        _state.postValue(state)
    }
}
