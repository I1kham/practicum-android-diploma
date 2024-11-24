package ru.practicum.android.diploma.presentation.city

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.city.CitySelectInteractor
import ru.practicum.android.diploma.domain.models.entity.Area
import ru.practicum.android.diploma.ui.city.model.CitySelectState
import java.net.SocketTimeoutException

class CitySelectViewModel(private val citySelectInteractor: CitySelectInteractor) : ViewModel() {
    private val _citySelectState = MutableLiveData<CitySelectState>()
    val citySelectState: LiveData<CitySelectState> = _citySelectState
    private var areasList: MutableList<Area> = mutableListOf()

    init {
        // получить айди страны
        // если айди пустой, то вывести все ареи
        getAllAreas()
        // если не пустой, то вывести ареи принадлежащие этому айди
        getCitiesById("2019") // тестовое айди, потом полученное внести "2019"

    }

    fun chooseArea() = { area: Area ->
        println(area)
        saveToFilter(area)
        pushState(CitySelectState.Exit)
    }

    private fun saveToFilter(area: Area) {
        // что то, что сохранит в фильтр данные
    }

    private fun getCitiesById(id: String) {
        viewModelScope.launch {
            try {
                citySelectInteractor.getCitiesByAreaId(id).collect { resource ->
                    resource?.data?.let { listAreas ->
                        areasList.addAll(listAreas)
                        pushState(CitySelectState.Success(areasList))
                    } ?: pushState(CitySelectState.Error)
                }
            } catch (e: SocketTimeoutException) {
                this.coroutineContext.job.cancel()
                handleErrorSocketTimeoutException(e)

            }
        }
    }

    private fun getAllAreas() {
        viewModelScope.launch {
            try {
                citySelectInteractor.getAllArea().collect { resource ->
                    resource?.data?.let { listAreas ->
                        areasList.addAll(listAreas)
                        pushState(CitySelectState.Success(listAreas))
                    } ?: pushState(CitySelectState.Error)
                }
            } catch (e: SocketTimeoutException) {
                this.coroutineContext.job.cancel()
                handleErrorSocketTimeoutException(e)

            }
        }
    }

    private fun pushState(state: CitySelectState) {
        _citySelectState.postValue(state)
    }

    private fun handleErrorSocketTimeoutException(e: SocketTimeoutException) {
        handleError()
        Log.e("SearchViewModel", "SocketTimeoutException, $e")
    }

    private fun handleError() {
        pushState(CitySelectState.Error)
    }

    fun filterRegions(text: String) {
        val filteredRegions = areasList.filter { area: Area ->
            area.name?.lowercase()?.contains(text.lowercase()) == true
        }
        if (filteredRegions.isEmpty()) {
            pushState(
                CitySelectState.Empty
            )
        } else {
            pushState(
                CitySelectState.Success(filteredRegions)
            )
        }
    }
}
