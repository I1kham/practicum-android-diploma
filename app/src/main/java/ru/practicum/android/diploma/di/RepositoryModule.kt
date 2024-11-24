package ru.practicum.android.diploma.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.practicum.android.diploma.data.convertors.FavoriteVacancyDbConverter
import ru.practicum.android.diploma.data.convertors.VacancyDtoConvertor
import ru.practicum.android.diploma.data.repository.CitySelectRepositoryImpl
import ru.practicum.android.diploma.data.repository.FavoritesVacancyRepositoryImpl
import ru.practicum.android.diploma.data.repository.HhRepositoryImpl
import ru.practicum.android.diploma.data.repository.IndustriesRepositoryImpl
import ru.practicum.android.diploma.data.repository.VacancySharingRepositoryImpl
import ru.practicum.android.diploma.domain.api.FavoritesVacancyRepository
import ru.practicum.android.diploma.domain.api.city.CitySelectRepository
import ru.practicum.android.diploma.domain.api.industries.IndustriesRepository
import ru.practicum.android.diploma.domain.api.hh.HhRepository
import ru.practicum.android.diploma.domain.api.sharing.VacancySharingRepository

val repositoryModule = module {
    single<VacancyDtoConvertor> {
        VacancyDtoConvertor()
    }
    single<FavoriteVacancyDbConverter> {
        FavoriteVacancyDbConverter(gson = get())
    }

    single<HhRepository> {
        HhRepositoryImpl(
            networkClient = get(),
            vacancyDtoConvertor = get()
        )
    }

    single<VacancySharingRepository> {
        VacancySharingRepositoryImpl(
            context = androidContext()
        )
    }
    single<FavoritesVacancyRepository> {
        FavoritesVacancyRepositoryImpl(
            favoritesVacancyDao = get(),
            favoriteVacancyDbConverter = get()
        )
    }

    single<IndustriesRepository> {
        IndustriesRepositoryImpl(
            networkClient = get()
        )
    }
    single<CitySelectRepository> {
        CitySelectRepositoryImpl(networkClient = get())
    }
}
