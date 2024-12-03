package ru.practicum.android.diploma.data.network

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.practicum.android.diploma.data.dto.request.CitiesByAreaIdRequest
import ru.practicum.android.diploma.data.dto.request.CountriesRequest
import ru.practicum.android.diploma.data.dto.request.VacanciesSearchRequest
import ru.practicum.android.diploma.data.dto.request.VacancyByIdRequest
import ru.practicum.android.diploma.data.dto.response.CityResponse
import ru.practicum.android.diploma.data.dto.response.IndustriesResponse
import ru.practicum.android.diploma.data.dto.response.Response
import ru.practicum.android.diploma.data.dto.response.VacancyResponse
import ru.practicum.android.diploma.data.dto.vacancy.AreaData
import ru.practicum.android.diploma.ui.root.RootActivity.Companion.NOT_DESIRED_AREA_KEY
import ru.practicum.android.diploma.util.ResponseStatusCode
import ru.practicum.android.diploma.util.isNetworkAvailable

class RetrofitNetworkClient(
    private val hhService: HhApiService,
    private val context: Context,
) : NetworkClient {
    override suspend fun getVacancies(dto: VacanciesSearchRequest): Response {
        return withContext(Dispatchers.IO) {
            if (!isConnected()) {
                Response().apply { resultCode = ResponseStatusCode.NoInternet }
            } else {
                withContext(Dispatchers.IO) {
                    try {
                        val response = hhService.searchVacancies(
                            dto.expression
                        )
                        response.apply { resultCode = ResponseStatusCode.Ok }
                    } catch (e: HttpException) {
                        println(e)
                        Response().apply { resultCode = ResponseStatusCode.Error }
                    }
                }
            }
        }
    }

    override suspend fun getVacancyById(dto: VacancyByIdRequest): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = ResponseStatusCode.NoInternet }
        } else {
            return withContext(Dispatchers.IO) {
                try {
                    VacancyResponse(
                        hhService.searchVacancyById(
                            dto.id
                        )
                    ).apply { resultCode = ResponseStatusCode.Ok }
                } catch (e: HttpException) {
                    println(e)
                    Response().apply { resultCode = ResponseStatusCode.Error }
                }
            }
        }
    }

    override suspend fun getIndustriesList(): Response {
        if (!isConnected()) {
            // если нет интернета возврат -1
            return Response().apply { resultCode = ResponseStatusCode.NoInternet }
        }
        return withContext(Dispatchers.IO) {
            try {
                val industriesList = hhService.getIndustriesList()
                IndustriesResponse.fromList(industriesList).apply {
                    resultCode = ResponseStatusCode.Ok
                }
            } catch (e: HttpException) {
                println(e)
                Response().apply {
                    resultCode = ResponseStatusCode.Error
                }
            }
        }
    }

    override suspend fun getCitiesBiAreaId(dto: CitiesByAreaIdRequest): Response {
        return if (!isConnected()) {
            // если нет интернета возврат -1
            Response().apply { resultCode = ResponseStatusCode.NoInternet }
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val response = hhService.getCitiesByAreaId(dto.areaId)
                    response.apply { resultCode = ResponseStatusCode.Ok }
                } catch (e: HttpException) {
                    println(e)
                    Response().apply { resultCode = ResponseStatusCode.Error }
                }
            }
        }
    }

    override suspend fun getAllArea(): Response {
        return if (!isConnected()) {
            // если нет интернета возврат -1
            Response().apply { resultCode = ResponseStatusCode.NoInternet }
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val list: MutableList<AreaData> = mutableListOf()
                    hhService.getAllArea().map { cityResponse ->
                        cityResponse.areas.map { areaData ->
                            if (areaData.parentId != NOT_DESIRED_AREA_KEY) {
                                list.add(areaData.copy(parentName = cityResponse.name))
                            }
                        }
                    }
                    val response = CityResponse(null, null, list)
                    response.apply { resultCode = ResponseStatusCode.Ok }
                } catch (e: HttpException) {
                    println(e)
                    Response().apply { resultCode = ResponseStatusCode.Error }
                }
            }
        }
    }

    override suspend fun getCountries(dto: CountriesRequest): Response {
        return if (!isConnected()) {
            Response().apply { resultCode = ResponseStatusCode.NoInternet }
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val response = hhService.searchCountries()
                    val list = mutableListOf<AreaData>()
                    response.map { response ->
                        list.add(
                            AreaData(
                                id = response.id,
                                parentId = null,
                                parentName = null,
                                name = response.name,
                                url = null
                            )
                        )
                    }
                    CityResponse(
                        id = null,
                        name = null,
                        areas = list.toList()
                    ).apply { resultCode = ResponseStatusCode.Ok }
                } catch (e: HttpException) {
                    println(e)
                    Response().apply { resultCode = ResponseStatusCode.Error }
                }
            }
        }
    }

    private fun isConnected(): Boolean {
        return isNetworkAvailable(context)
    }
}
