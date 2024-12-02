package ru.practicum.android.diploma.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.convertors.VacancyDtoConvertor
import ru.practicum.android.diploma.data.dto.request.CountriesRequest
import ru.practicum.android.diploma.data.dto.request.VacanciesSearchRequest
import ru.practicum.android.diploma.data.dto.request.VacancyByIdRequest
import ru.practicum.android.diploma.data.dto.response.CityResponse
import ru.practicum.android.diploma.data.dto.response.VacanciesResponse
import ru.practicum.android.diploma.data.dto.response.VacancyResponse
import ru.practicum.android.diploma.data.dto.vacancy.VacancyData
import ru.practicum.android.diploma.data.dto.vacancy.map
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.hh.HhRepository
import ru.practicum.android.diploma.domain.models.entity.Area
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.util.Resource
import ru.practicum.android.diploma.util.ResponseStatusCode

class HhRepositoryImpl(
    private val networkClient: NetworkClient,
    private val vacancyDtoConvertor: VacancyDtoConvertor,
) : HhRepository {

    override suspend fun getVacancies(expression: HashMap<String, String>): Flow<Resource<List<Vacancy>>?> = flow {
        val response = networkClient.getVacancies(VacanciesSearchRequest(expression))

        when (response.resultCode) {
            is ResponseStatusCode.NoInternet -> {
                emit(Resource.Error(ResponseStatusCode.NoInternet))
            }

            is ResponseStatusCode.Ok -> {
                emit(
                    Resource.Success(
                        data = (response as VacanciesResponse).vacancies.let { list: List<VacancyData> ->
                            list.map { vacancyData: VacancyData ->
                                vacancyDtoConvertor.map(vacancyData)
                            }
                        },
                        responseCode = response.resultCode,
                        currentPage = response.page,
                        pagesCount = response.pages,
                        foundedCount = response.found
                    )
                )
            }

            is ResponseStatusCode.Error -> {
                emit(Resource.Error(ResponseStatusCode.Error))
            }

            else -> {
                emit(Resource.Error(ResponseStatusCode.Error))
            }
        }
    }

    override suspend fun searchVacanceById(id: String): Flow<Resource<Vacancy?>> = flow {
        val response = networkClient.getVacancyById(VacancyByIdRequest(id))
        when (response.resultCode) {
            is ResponseStatusCode.NoInternet -> {
                emit(Resource.Error(ResponseStatusCode.NoInternet))
            }

            is ResponseStatusCode.Ok -> {
                (response as VacancyResponse).vacancyData?.let {
                    emit(
                        Resource.Success(
                            data = vacancyDtoConvertor.run {
                                map(it)
                            }, responseCode = response.resultCode
                        )
                    )
                } ?: emit(Resource.Error(ResponseStatusCode.Error))

            }

            is ResponseStatusCode.Error -> {
                emit(Resource.Error(ResponseStatusCode.Error))
            }

            else -> {
                emit(Resource.Error(ResponseStatusCode.Error))
            }
        }
    }

    override suspend fun searchCountries(): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.getCountries(CountriesRequest())
        when (response.resultCode) {
            is ResponseStatusCode.NoInternet -> {
                emit(Resource.Error(ResponseStatusCode.NoInternet))
            }

            is ResponseStatusCode.Ok -> {
                if (response is CityResponse) {
                    emit(
                        Resource.Success(
                            response.areas.map { areaData ->
                                areaData.map()
                            }
                        )
                    )
                }
            }

            is ResponseStatusCode.Error -> {
                emit(Resource.Error(ResponseStatusCode.Error))
            }

            else -> {
                emit(Resource.Error(ResponseStatusCode.Error))
            }
        }
    }

}
