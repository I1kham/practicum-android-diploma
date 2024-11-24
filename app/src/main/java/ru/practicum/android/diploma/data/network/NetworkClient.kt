package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.request.CitiesByAreaIdRequest
import ru.practicum.android.diploma.data.dto.request.VacanciesSearchRequest
import ru.practicum.android.diploma.data.dto.request.VacancyByIdRequest
import ru.practicum.android.diploma.data.dto.response.Response

interface NetworkClient {
    suspend fun getVacancies(dto: VacanciesSearchRequest): Response
    suspend fun getVacancyById(dto: VacancyByIdRequest): Response
    suspend fun getIndustriesList(): Response
    suspend fun getCitiesBiAreaId(dto: CitiesByAreaIdRequest): Response
    suspend fun getAllArea(): Response
}
