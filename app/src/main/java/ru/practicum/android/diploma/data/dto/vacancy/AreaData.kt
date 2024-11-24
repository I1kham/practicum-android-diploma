package ru.practicum.android.diploma.data.dto.vacancy

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.domain.models.entity.Area

data class AreaData(
    val id: String?,
    @SerializedName("parent_id")
    val parentId: String?,
    val name: String?,
    val url: String?,
)

fun AreaData.map(): Area {
    return Area(
        this.id,
        this.parentId,
        this.name,
        this.url
    )
}
