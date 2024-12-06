package ru.practicum.android.diploma.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.favorite.FavoritesInteractor
import ru.practicum.android.diploma.domain.api.hh.HhInteractor
import ru.practicum.android.diploma.domain.api.sharing.VacancySharingInteractor
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.ui.details.models.DetailsFragmentState
import ru.practicum.android.diploma.util.Resource
import ru.practicum.android.diploma.util.ResponseStatusCode

class DetailsFragmentViewModel(
    private val hhInteractor: HhInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val vacancySharingInteractor: VacancySharingInteractor,
) : ViewModel() {
    private var vacancy: Vacancy? = null
    private var responseStatusCode: ResponseStatusCode? = null
    private val stateLiveData = MutableLiveData<DetailsFragmentState>()

    fun observeState(): LiveData<DetailsFragmentState> = stateLiveData

    //    liveData для иконки добавления в избранное
    private var _isFavoriteLiveData = MutableLiveData<Boolean>()
    val isFavoriteLiveData: LiveData<Boolean> = _isFavoriteLiveData

    private fun renderState(state: DetailsFragmentState) {
        stateLiveData.postValue(state)
    }

    // изменил на изначальную загрузку с интернета
    fun getVacancy(id: String?) {
        id?.let {
            viewModelScope.launch {
                hhInteractor.searchVacanceById(id).collect { resource: Resource<Vacancy?> ->
                    when (resource.responseCode) {
                        ResponseStatusCode.Ok -> {
                            resource.data?.let {
                                vacancy = it
                                renderState(DetailsFragmentState.Content(it))
                                _isFavoriteLiveData.postValue(favoritesInteractor.isFavoriteCheck(id))
                            } ?: renderState(DetailsFragmentState.Empty)
                        }

                        else -> {
                            responseStatusCode = resource.responseCode
                            getVacancyBd(id)
                        }
                    }
                }
            }
        } ?: renderState(DetailsFragmentState.Error(ResponseStatusCode.Error))
    }

    private suspend fun getVacancyBd(id: String) {
        val cachedVacancy = favoritesInteractor.getFavoriteVacancyById(id)
        cachedVacancy?.let {
            vacancy = it
            renderState(DetailsFragmentState.Content(it))
            _isFavoriteLiveData.postValue(favoritesInteractor.isFavoriteCheck(id))
        } ?: renderState(DetailsFragmentState.Error(responseStatusCode))
    }

    fun shareVacancy() {
        viewModelScope.launch {
            vacancy?.let {
                vacancySharingInteractor.shareVacancy(it)
            }
        }
    }

    fun likeButton() {
        val currentVacancy = vacancy ?: return
        viewModelScope.launch {
            if (favoritesInteractor.isFavoriteCheck(currentVacancy.id)) {
                favoritesInteractor.deleteVacancy(currentVacancy)
                _isFavoriteLiveData.postValue(false)
            } else {
                favoritesInteractor.insertVacancy(currentVacancy)
                _isFavoriteLiveData.postValue(true)
            }
        }
    }
}
