package ru.practicum.android.diploma.data.dto.response

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.data.dto.vacancy.AreaData

class CityResponse(
    @SerializedName("areas")
    val areas: List<AreaData>
) : Response()
