package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.api.FavoritesInteractor
import ru.practicum.android.diploma.domain.api.city.CitySelectInteractor
import ru.practicum.android.diploma.domain.api.hh.HhInteractor
import ru.practicum.android.diploma.domain.api.industries.IndustriesInteractor
import ru.practicum.android.diploma.domain.api.sharing.VacancySharingInteractor
import ru.practicum.android.diploma.domain.impl.CitySelectInteractorImpl
import ru.practicum.android.diploma.domain.impl.FavoritesInteractorImpl
import ru.practicum.android.diploma.domain.impl.HhInteractorImpl
import ru.practicum.android.diploma.domain.impl.IndustriesInteractorImpl
import ru.practicum.android.diploma.domain.impl.VacancySharingInteractorImpl

val interactorModule = module {
    single<HhInteractor> {
        HhInteractorImpl(
            hhRepository = get()
        )
    }
    single<VacancySharingInteractor> {
        VacancySharingInteractorImpl(
            vacancySharingRepository = get()
        )
    }
    single<FavoritesInteractor> {
        FavoritesInteractorImpl(
            repository = get()
        )
    }
    single<IndustriesInteractor> {
        IndustriesInteractorImpl(
            industriesRepository = get()
        )
    }
    single<CitySelectInteractor> {
        CitySelectInteractorImpl(
            citySelectRepository = get()
        )
    }
}
