package ru.practicum.android.diploma.presentation.card.vacancy

import androidx.recyclerview.widget.DiffUtil
import ru.practicum.android.diploma.domain.models.entity.Vacancy

class VacancyDiffCallback : DiffUtil.ItemCallback<Vacancy>() {
    override fun areItemsTheSame(oldItem: Vacancy, newItem: Vacancy) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Vacancy, newItem: Vacancy) = oldItem == newItem
}
