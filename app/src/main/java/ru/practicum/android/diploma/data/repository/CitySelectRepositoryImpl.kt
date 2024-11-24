package ru.practicum.android.diploma.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.dto.request.CitiesByAreaIdRequest
import ru.practicum.android.diploma.data.dto.response.CityResponse
import ru.practicum.android.diploma.data.dto.vacancy.map
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.domain.api.city.CitySelectRepository
import ru.practicum.android.diploma.domain.models.entity.Area
import ru.practicum.android.diploma.util.Resource
import ru.practicum.android.diploma.util.ResponseStatusCode

class CitySelectRepositoryImpl(
    private val networkClient: NetworkClient,
) : CitySelectRepository {
    override suspend fun getCitiesByAreaId(id: String): Flow<Resource<List<Area>>?> = flow {
        val response = networkClient.getCitiesBiAreaId(CitiesByAreaIdRequest(id))
        when (response.resultCode) {
            is ResponseStatusCode.NoInternet -> {
                emit(Resource.Error(ResponseStatusCode.NoInternet))
            }

            is ResponseStatusCode.Ok -> {
                emit(
                    Resource.Success(
                        (response as CityResponse).areas.map { areaData ->
                            areaData.map()
                        }
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

    override suspend fun getAllArea(): Flow<Resource<List<Area>>?> = flow {
        val response = networkClient.getAllArea()
        when (response.resultCode) {
            is ResponseStatusCode.NoInternet -> {
                emit(Resource.Error(ResponseStatusCode.NoInternet))
            }

            is ResponseStatusCode.Ok -> {
                emit(
                    Resource.Success(
                        (response as CityResponse).areas.map { areaData ->
                            areaData.map()
                        }
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
}
