package ru.practicum.android.diploma.domain.api

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.data.dto.response.VacancyResponse
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.util.Resource

interface HhRepository {
    suspend fun getVacancies(expression: HashMap<String, String>): Flow<Resource<List<Vacancy>>>

    suspend fun searchVacanceById(id: String): Flow<Resource<Vacancy>>
}
