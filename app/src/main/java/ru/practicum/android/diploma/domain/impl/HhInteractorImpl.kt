package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.hh.HhInteractor
import ru.practicum.android.diploma.domain.api.hh.HhRepository
import ru.practicum.android.diploma.domain.models.entity.Area
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.util.Resource

class HhInteractorImpl(val hhRepository: HhRepository) : HhInteractor {
    override suspend fun getVacancies(expression: HashMap<String, String>): Flow<Resource<List<Vacancy>>?> {
        return hhRepository.getVacancies(expression)
    }

    override suspend fun searchVacanceById(id: String): Flow<Resource<Vacancy?>> {
        return hhRepository.searchVacanceById(id)
    }

    override suspend fun searchCountries(): Flow<Resource<List<Area>>> {
        return hhRepository.searchCountries()
    }

}
