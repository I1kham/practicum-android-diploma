package ru.practicum.android.diploma.ui.country.model

import ru.practicum.android.diploma.domain.models.entity.Area

sealed interface CountrySelectState {
    data object Loading : CountrySelectState
    data class Success(val countries: List<Area>) : CountrySelectState
    data object Error : CountrySelectState
    data object NoInternet : CountrySelectState
    data object Empty : CountrySelectState
    data object Exit : CountrySelectState

}
