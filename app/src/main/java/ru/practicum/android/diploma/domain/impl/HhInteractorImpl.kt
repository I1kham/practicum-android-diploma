package ru.practicum.android.diploma.domain.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.api.HhInteractor
import ru.practicum.android.diploma.domain.api.HhRepository
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.util.Resource

class HhInteractorImpl(val hhRepository: HhRepository) : HhInteractor {
    override suspend fun getVacancies(expression: HashMap<String, String>): Flow<Resource<List<Vacancy>>> {
        return hhRepository.getVacancies(expression)
    }

    override suspend fun searchVacanceById(id: String): Flow<Result<Vacancy>> {
        return hhRepository.searchVacanceById(id)
    }


}
