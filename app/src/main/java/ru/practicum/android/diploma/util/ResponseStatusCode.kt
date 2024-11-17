package ru.practicum.android.diploma.util

sealed interface ResponseStatusCode {
    data object OK : ResponseStatusCode
    data object ERROR : ResponseStatusCode
    data object NO_INTERNET : ResponseStatusCode

}
