package ru.practicum.android.diploma.presentation.card.vacancy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.practicum.android.diploma.databinding.JobItemBinding
import ru.practicum.android.diploma.domain.models.entity.Vacancy

class VacancyAdapter(private val list: List<Vacancy>) :
    ListAdapter<Vacancy, VacancyViewHolder>(VacancyDiffCallback()) {
    var onItemClick = { _: Vacancy -> }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacancyViewHolder {
        val binding = JobItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VacancyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener { onItemClick.invoke(list[position]) }
    }

    override fun getItemCount() = list.size
}
