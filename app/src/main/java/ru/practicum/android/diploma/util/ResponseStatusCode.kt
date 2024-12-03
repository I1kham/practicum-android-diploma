package ru.practicum.android.diploma.util

sealed interface ResponseStatusCode {
    data object Ok : ResponseStatusCode
    data object Error : ResponseStatusCode
    data object NoInternet : ResponseStatusCode
}
