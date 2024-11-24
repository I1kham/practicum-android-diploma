package ru.practicum.android.diploma.presentation.card.text

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.TextItemBinding
import ru.practicum.android.diploma.domain.models.entity.Area
import ru.practicum.android.diploma.util.fillBy
import ru.practicum.android.diploma.util.format

class TextCardViewHolder(private val binding: TextItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(area: Area) {
        binding.apply {
            cardDescriptionText.text = area.name
        }
    }

}
