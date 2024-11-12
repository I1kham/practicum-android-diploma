package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.request.VacanciesSearchRequest
import ru.practicum.android.diploma.data.dto.response.Response

interface NetworkClient {
    suspend fun getVacancies(dto: VacanciesSearchRequest): Response
}