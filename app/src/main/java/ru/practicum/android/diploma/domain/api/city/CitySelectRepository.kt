package ru.practicum.android.diploma.domain.api.city

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.entity.Area
import ru.practicum.android.diploma.util.Resource

interface CitySelectRepository {
    suspend fun getCitiesByAreaId(id: String): Flow<Resource<List<Area>>?>
}
