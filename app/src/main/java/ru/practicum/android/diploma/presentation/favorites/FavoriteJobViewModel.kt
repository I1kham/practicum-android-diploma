package ru.practicum.android.diploma.presentation.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.api.favorite.FavoritesInteractor
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.ui.favorites.models.FavoritesState

class FavoriteJobViewModel(
    private val interactor: FavoritesInteractor,
) : ViewModel() {

    private val _favoritesState = MutableLiveData<FavoritesState>()
    val favoritesState: LiveData<FavoritesState> = _favoritesState

    fun getVacancies() {
        viewModelScope.launch {
            interactor.getFavoriteVacancies()
                .onStart { pushFavoriteState(FavoritesState.Loading) }
                .catch { _ ->
                    pushFavoriteState(
                        FavoritesState.Error(
                            R.string.couldnt_get_job_list
                        )
                    )
                } // обработка ошибок
                .collect { vacancies ->
                    showData(vacancies.reversed())
                }
        }
    }

    private fun showData(vacancies: List<Vacancy>) {
        if (vacancies.isEmpty()) {
            pushFavoriteState(FavoritesState.Empty(R.string.empty_list))
        } else {
            pushFavoriteState(FavoritesState.Content(vacancies))
        }
    }

    // функция для обновления liveData
    private fun pushFavoriteState(state: FavoritesState) {
        _favoritesState.postValue(state)
    }
}
