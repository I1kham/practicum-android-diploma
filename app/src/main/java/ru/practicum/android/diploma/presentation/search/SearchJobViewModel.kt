package ru.practicum.android.diploma.presentation.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.api.filter.FilterInteractor
import ru.practicum.android.diploma.domain.api.hh.HhInteractor
import ru.practicum.android.diploma.domain.models.entity.FilterShared
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.ui.search.models.VacanciesState
import ru.practicum.android.diploma.util.Resource
import ru.practicum.android.diploma.util.ResponseStatusCode
import ru.practicum.android.diploma.util.debounce
import java.net.SocketTimeoutException

class SearchJobViewModel(
    private val hhInteractor: HhInteractor,

    private val filterInteractor: FilterInteractor
) : ViewModel() {
    private val _vacanciesState = MutableLiveData<VacanciesState>()

    val vacanciesState: LiveData<VacanciesState> = _vacanciesState
    private val _savedFilter = MutableLiveData<FilterShared?>()
    val savedFilter: LiveData<FilterShared?> = _savedFilter

    // переменные для работы с paggination
    private var currentPage = 0
    private var maxPage = 0
    private var currentQuery = ""
    private var isLastPage = false
    private var isLoading = false
    private var vacanciesList = mutableListOf<Vacancy>()

    private val searchDebounce = debounce<String>(
        delayMillis = DEBOUNCE_SEARCH_TIME,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { searchText ->
        searchVacancies(searchText.trim())
    }
    private val loadPerPageDebounce = debounce<Int>(
        delayMillis = DEBOUNCE_PAGE_TIME,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { page ->
        currentPage = page + 1
        loadVacancies()
    }

    fun clearVacancies() {
        pushVacanciesState(VacanciesState.Start)
        currentPage = 0
        maxPage = 0
        currentQuery = ""
        isLastPage = false
        isLoading = false
        vacanciesList.clear()
    }

    // эта ф-ия берет запрос из EditText и запрашивает данные с сервека через hhInteractor
    private fun searchVacancies(query: String) {
        if (currentQuery != query) {
            println("current: $currentQuery, query: $query")
            currentPage = 0
            maxPage = 0
            isLastPage = false
            vacanciesList.clear()
            currentQuery = query
            loadVacancies()
        }
    }

    private fun pushVacanciesState(state: VacanciesState) {
        _vacanciesState.postValue(state)
    }

    private fun loadVacancies() {
        if (currentQuery.trim().isNotEmpty()) {
            isLoading = true
            val params = createParams()
            pushVacanciesState(VacanciesState.Loading)
            viewModelScope.launch {
                isLoading = true
                try {
                    hhInteractor.getVacancies(params).collect { result ->
                        handleResult(result)
                        isLoading = false
                    }
                } catch (e: SocketTimeoutException) {
                    handleErrorSocketTimeoutException(e)
                    this.coroutineContext.job.cancel()
                }
            }
        }
    }

    fun searchDebounced(changedText: String) {
        if (changedText != currentQuery) {
            searchDebounce(changedText)
        }
    }

    // Ф-ия для Fragment для загрузки следующей страницы
    fun loadNextPage() {
        if (!isLoading && !isLastPage && currentQuery.isNotEmpty()) {
            isLoading = true
            loadPerPageDebounce(currentPage)
        }
    }

    private fun createParams(): HashMap<String, String> {
        return hashMapOf(
            "text" to currentQuery,
            "page" to currentPage.toString(),
            "per_page" to PAGE_SIZE.toString()
        ).apply {
            _savedFilter.value?.regionId?.let { put("area", it) }
            _savedFilter.value?.industryId?.let { put("industry", it) }
            _savedFilter.value?.salary?.let { put("salary", it) }
            _savedFilter.value?.onlySalaryFlag?.let { put("only_with_salary", it.toString()) }
        }
    }

    private fun handleResult(result: Resource<List<Vacancy>>?) {
        when (result) {
            is Resource.Success -> handleSuccess(result)
            is Resource.Error -> handleError(result.responseCode)
            else -> handleError(result?.responseCode)
        }
    }

    private fun handleSuccess(result: Resource<List<Vacancy>>?) {
        result?.currentPage?.let {
            currentPage = result.currentPage
        }
        result?.pagesCount?.let {
            maxPage = result.pagesCount
        }
        val newVacancies = result?.data.orEmpty()
        isLastPage = currentPage == maxPage - 1
        if (newVacancies.isNotEmpty()) {
            vacanciesList.addAll(newVacancies)
            pushVacanciesState(
                VacanciesState.Success(
                    vacancies = vacanciesList.toList(),
                    isLastPage = isLastPage,
                    isLoading = false,
                    result?.foundedCount
                )
            )
        } else {
            pushVacanciesState(VacanciesState.Empty)
        }
    }

    private fun handleErrorSocketTimeoutException(e: SocketTimeoutException) {
        handleError(ResponseStatusCode.Error)
        Log.e("SearchViewModel", "SocketTimeoutException, $e")
    }

    private fun handleError(responseCode: ResponseStatusCode?) {
        pushVacanciesState(VacanciesState.Error(responseCode))
    }

    fun getFilter() {
        viewModelScope.launch {
            val filter = filterInteractor.getFilter()
            _savedFilter.value = filter
            if (filter?.apply == true) {
                pushVacanciesState(VacanciesState.Start)
                currentPage = 0
                maxPage = 0
                isLastPage = false
                isLoading = false
                vacanciesList.clear()
                loadVacancies()
                filterInteractor.saveFilter(filter.copy(apply = null))
            }
        }
    }

    companion object {
        private const val DEBOUNCE_SEARCH_TIME = 2000L
        private const val DEBOUNCE_PAGE_TIME = 300L
        private const val PAGE_SIZE = 20 // кол-во элементов на странице для отображения в RV
    }
}
