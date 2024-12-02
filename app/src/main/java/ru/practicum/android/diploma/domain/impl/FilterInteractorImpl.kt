package ru.practicum.android.diploma.domain.impl

import ru.practicum.android.diploma.domain.api.filter.FilterInteractor
import ru.practicum.android.diploma.domain.api.filter.FilterRepository
import ru.practicum.android.diploma.domain.models.entity.FilterShared

class FilterInteractorImpl(private val filterRepository: FilterRepository) : FilterInteractor {
    override suspend fun getFilter(): FilterShared? {
        return filterRepository.getFilter()
    }

    override suspend fun saveFilter(filterShared: FilterShared?) {
        return filterRepository.saveFilter(filterShared)
    }

    override suspend fun getTempFilter(): FilterShared? {
        return filterRepository.getTempFilter()
    }

    override suspend fun saveTempFilter(filterShared: FilterShared?) {
        return filterRepository.saveTempFilter(filterShared)
    }
}
