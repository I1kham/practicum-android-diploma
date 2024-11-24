package ru.practicum.android.diploma.domain.models.entity

data class Vacancy(
    // Для JobItem
    val id: String, // id вакансии
    val name: String?, // название вакансии
    val employer: Employer?, // работодатель
    val salary: Salary?, // зарплата
    val adress: Address?,

    // Для Описания вакансии
    val experience: Experience?, // опыт
    val employment: Employment?, // тип занятости
    val keySkills: List<KeySkill>?, // ключевые навыки
    val languages: List<Language>?, // языки
    val driverLicenseTypes: List<DriverLicense>?, // категория прав водителя
    val area: Area?,
    val industry: Industry?,
    val country: Country?,
    val contacts: Contacts?,
    val description: String?,
    val schedule: Schedule?,
    val url: String?,
)

class Schedule(
    val id: String?,
    val name: String?,
)

data class Contacts(
    val email: String?,
    val name: String?,
    val phones: List<Phone>?, // = null,
)

class Phone(
    val city: String,
    val comment: String?, // = null,
    val country: String,
    val formatted: String,
    val number: String,
)

data class MetroStations(
    val lineName: String?,
    val stationName: String?,
)

data class Country(
    val id: String,
    val name: String,
)

data class Industry(
    val id: String,
    val name: String,
    val industries: List<IndustryNested>?,
)

data class IndustryNested(
    val id: String?,
    val name: String?,
)

data class Area(
    val id: String?,
    val parentId: String?,
    val name: String?,
    val url: String?,
)

data class Address(
    val building: String?,
    val city: String?,
    val description: String?,
    val metro: List<MetroStations>?,
    val street: String?,
    val full: String?,
)

data class Employer(
    val name: String?, // название работодателя
    val logoUrls: LogoUrls?, // Иконки работодателя
)

data class Salary(
    val currency: String?, // валюта
    val from: Int?, // сумма от
    val gross: Boolean?, // до или после вычета
    val to: Int?, // сумма до
)

data class LogoUrls(
    val small: String?, // Маленькая иконка
    val medium: String?, // Большая иконка
    val original: String?, // стандартная иконка
)

data class Experience(
    val name: String?, // Строка "требуемый опыт"
)

data class Employment(
    val name: String?, // строка "тип занятости"
)

data class KeySkill(
    val name: String?, // Описание(Обязанности)
)

data class Language(
    val name: String?, // Название языка
    val level: LanguageLevel?,
)

data class LanguageLevel(
    val name: String?, // Уровень языка
)

data class DriverLicense(
    val id: String?, // название категории
)
