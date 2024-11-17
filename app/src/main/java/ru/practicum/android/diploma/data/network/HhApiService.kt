package ru.practicum.android.diploma.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.data.dto.response.VacanciesResponse
import ru.practicum.android.diploma.data.dto.response.VacancyResponse

interface HhApiService {
    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: YpDiplomaProject/1.0 (4habibulin@gmail.com)"
    )

    @GET("vacancies")
    suspend fun searchVacancies(
        @QueryMap params: HashMap<String, String>,
    ): Response<VacanciesResponse>

    @GET("vacancies/{id}")
    suspend fun searchVacanceById(@Path("id") id: String): VacancyResponse
}
