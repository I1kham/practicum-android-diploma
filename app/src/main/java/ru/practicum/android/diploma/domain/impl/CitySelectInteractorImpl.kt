package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.city.CitySelectInteractor
import ru.practicum.android.diploma.domain.api.city.CitySelectRepository
import ru.practicum.android.diploma.domain.models.entity.Area
import ru.practicum.android.diploma.util.Resource

class CitySelectInteractorImpl(val citySelectRepository: CitySelectRepository) : CitySelectInteractor {
    override suspend fun getCitiesByAreaId(id: String): Flow<Resource<List<Area>>?> {
        return citySelectRepository.getCitiesByAreaId(id)
    }

    override suspend fun getAllArea(): Flow<Resource<List<Area>>?> {
        return citySelectRepository.getAllArea()
    }
}
