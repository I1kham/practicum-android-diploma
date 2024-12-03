package ru.practicum.android.diploma.domain.models.entity

data class FilterShared(
    val countryName: String?,
    val countryId: String?,
    val regionName: String?,
    val regionId: String?,
    val industryName: String?,
    val industryId: String?,
    val salary: String?,
    val onlySalaryFlag: Boolean?,
    val apply: Boolean?
)

fun FilterShared?.isNotEmptyCheck(): Boolean {
    return this?.countryName != null ||
        this?.countryId != null ||
        this?.regionName != null ||
        this?.regionId != null ||
        this?.industryName != null ||
        this?.industryId != null ||
        this?.salary != null ||
        this?.onlySalaryFlag != null
}
