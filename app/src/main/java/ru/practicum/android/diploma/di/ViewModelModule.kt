package ru.practicum.android.diploma.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.city.CitySelectViewModel
import ru.practicum.android.diploma.presentation.details.DetailsFragmentViewModel
import ru.practicum.android.diploma.presentation.favorites.FavoriteJobViewModel
import ru.practicum.android.diploma.presentation.search.SearchJobViewModel
import ru.practicum.android.diploma.presentation.team.TeamViewModel

val viewModelModule = module {
    viewModel {
        SearchJobViewModel(
            hhInteractor = get()
        )
    }
    viewModel {
        TeamViewModel()
    }
    viewModel {
        FavoriteJobViewModel(
            interactor = get()
        )
    }
    viewModel {
        DetailsFragmentViewModel(
            hhInteractor = get(),
            favoritesInteractor = get(),
            vacancySharingInteractor = get(),
        )
    }
    viewModel {
        CitySelectViewModel(
            citySelectInteractor = get(),
        )
    }
}
