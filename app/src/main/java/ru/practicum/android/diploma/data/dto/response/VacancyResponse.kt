package ru.practicum.android.diploma.data.dto.response

import com.google.gson.annotations.SerializedName
import ru.practicum.android.diploma.data.dto.vacancy.AddressData
import ru.practicum.android.diploma.data.dto.vacancy.AreaData
import ru.practicum.android.diploma.data.dto.vacancy.ContactsData
import ru.practicum.android.diploma.data.dto.vacancy.CountryData
import ru.practicum.android.diploma.data.dto.vacancy.DriverLicenseData
import ru.practicum.android.diploma.data.dto.vacancy.EmployerData
import ru.practicum.android.diploma.data.dto.vacancy.EmploymentData
import ru.practicum.android.diploma.data.dto.vacancy.ExperienceData
import ru.practicum.android.diploma.data.dto.vacancy.IndustryData
import ru.practicum.android.diploma.data.dto.vacancy.KeySkillData
import ru.practicum.android.diploma.data.dto.vacancy.LanguageData
import ru.practicum.android.diploma.data.dto.vacancy.SalaryData
import ru.practicum.android.diploma.data.dto.vacancy.ScheduleData

class VacancyResponse(
    // Для JobItem
    val id: String, // id вакансии
    val name: String?, // название вакансии
    val employer: EmployerData?, // работодатель
    @SerializedName("salary") val salaryData: SalaryData?, // зарплата
    val adress: AddressData?,
    // Для Описания вакансии
    val experience: ExperienceData?, // опыт
    val employment: EmploymentData?, // тип занятости
    @SerializedName("key_skills") val keySkills: List<KeySkillData>?, // ключевые навыки
    val languages: List<LanguageData>?, // языки
    @SerializedName("driver_license_types") val driverLicenseTypes: List<DriverLicenseData>?, // категория прав водителя
    val area: AreaData?,
    val industry: IndustryData?,
    val country: CountryData?,
    val contacts: ContactsData?,
    val description: String?,
    val schedule: ScheduleData?,
    @SerializedName("alternate_url") val url: String?,
    ) : Response()
