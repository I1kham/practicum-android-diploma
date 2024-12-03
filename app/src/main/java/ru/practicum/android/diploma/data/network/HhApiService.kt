package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.data.dto.response.CityResponse
import ru.practicum.android.diploma.data.dto.response.CountriesResponse
import ru.practicum.android.diploma.data.dto.response.VacanciesResponse
import ru.practicum.android.diploma.data.dto.vacancy.IndustryData
import ru.practicum.android.diploma.data.dto.vacancy.VacancyData

interface HhApiService {

    @GET("vacancies")
    suspend fun searchVacancies(
        @QueryMap params: HashMap<String, String>,
    ): VacanciesResponse

    @GET("vacancies/{id}")
    suspend fun searchVacancyById(@Path("id") id: String): VacancyData?

    @GET("industries")
    suspend fun getIndustriesList(): List<IndustryData>

    @GET("/areas/{areas_id}")
    suspend fun getCitiesByAreaId(@Path("areas_id") areasId: String): CityResponse

    @GET("/areas")
    suspend fun getAllArea(): List<CityResponse>

    @GET("areas")
    suspend fun searchCountries(): List<CountriesResponse>
}
