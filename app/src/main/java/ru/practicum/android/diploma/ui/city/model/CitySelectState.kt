package ru.practicum.android.diploma.ui.city.model

import ru.practicum.android.diploma.domain.models.entity.Area

sealed interface CitySelectState {
    data class Success(val cities: List<Area>) : CitySelectState
    data object Error : CitySelectState
    data object Empty : CitySelectState
    data object Exit : CitySelectState
}
