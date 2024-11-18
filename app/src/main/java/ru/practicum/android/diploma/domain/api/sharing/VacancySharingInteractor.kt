package ru.practicum.android.diploma.domain.api.sharing

import ru.practicum.android.diploma.domain.models.entity.Vacancy

interface VacancySharingInteractor {
    suspend fun shareVacancy(vacancy: Vacancy)

}
