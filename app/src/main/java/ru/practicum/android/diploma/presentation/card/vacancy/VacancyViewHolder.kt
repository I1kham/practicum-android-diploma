package ru.practicum.android.diploma.presentation.card.vacancy

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.JobItemBinding
import ru.practicum.android.diploma.domain.models.entity.Vacancy
import ru.practicum.android.diploma.util.fillBy
import ru.practicum.android.diploma.util.format

class VacancyViewHolder(private val binding: JobItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(vacancy: Vacancy) {
        binding.apply {
            val vacancyName = vacancy.name
            val vacancyCity = vacancy.area?.name
            cardTitleText.text = if (vacancyCity != null) {
                itemView.context.getString(R.string.job_item_title_mask, vacancyName, vacancyCity)
            } else {
                vacancyName
            }
            cardDescriptionText.text = vacancy.employer?.name
            cardSalaryText.text = vacancy.salary.format()

            vacancy.employer?.logoUrls?.original.let { logoUrl ->
                cardImage.fillBy(logoUrl, itemView.context)
            }
        }
    }

}
