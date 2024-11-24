package ru.practicum.android.diploma.presentation.card.vacancy

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.JobItemBinding
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.util.fillBy
import ru.practicum.android.diploma.util.format

class VacancyViewHolder(private val binding: JobItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(vacancy: Vacancy) {
        binding.apply {
            cardTitleText.text = vacancy.name
            cardDescriptionText.text = vacancy.employer?.name
            cardSalaryText.text = vacancy.salary.format()

            vacancy.employer?.logoUrls?.original.let { logoUrl ->
                cardImage.fillBy(logoUrl, itemView.context)
            }
        }
    }

}
