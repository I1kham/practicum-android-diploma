package ru.practicum.android.diploma.domain.api.hh

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.entity.Area
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.util.Resource

interface HhRepository {
    suspend fun getVacancies(expression: HashMap<String, String>): Flow<Resource<List<Vacancy>>?>
    suspend fun searchCountries(): Flow<Resource<List<Area>>>
    suspend fun searchVacanceById(id: String): Flow<Resource<Vacancy?>>
}
