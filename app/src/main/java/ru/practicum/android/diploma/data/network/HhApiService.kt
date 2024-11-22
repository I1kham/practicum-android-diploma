package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.data.dto.response.IndustriesResponse
import ru.practicum.android.diploma.data.dto.response.VacanciesResponse
import ru.practicum.android.diploma.data.dto.vacancy.VacancyData

interface HhApiService {

    @GET("vacancies")
    suspend fun searchVacancies(
        @QueryMap params: HashMap<String, String>,
    ): VacanciesResponse

    @GET("vacancies/{id}")
    suspend fun searchVacancyById(@Path("id") id: String): VacancyData?


    @GET("industries")
    suspend fun getIndustriesList(): IndustriesResponse
}
