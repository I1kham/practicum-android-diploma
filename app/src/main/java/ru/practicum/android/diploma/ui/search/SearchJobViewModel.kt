package ru.practicum.android.diploma.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.HhInteractor
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.util.Resource

class SearchJobViewModel(private val hhInteractor: HhInteractor) : ViewModel() {

   private val _vacancies = MutableLiveData<List<Vacancy>>() // Отслеживаем входящие вакансии
    val vacancies: LiveData<List<Vacancy>> = _vacancies

    private var searchJob: Job? = null
        // в этой функции тестим поиск
    fun start() {
        searchVacancies("Повар")
        _vacancies.value?.map {Log.d("viewModel", "${it.name}")  }
    }
        // эта ф-ия берет запрос из EditText и запрашивает данные с сервека через hhInteractor
    fun searchVacancies(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(2000) // Реализован debounce 2 сек
            hhInteractor.getVacancies(hashMapOf("text" to query)).collect { result ->
                when(result) {
                    is Resource.Success -> {
                        _vacancies.value = result.data?: emptyList()
                    }
                    is Resource.Error -> {
                        // Если ошибка - прокинуть как переменную в UI
                    }
                }

            }
        }
    }
}
