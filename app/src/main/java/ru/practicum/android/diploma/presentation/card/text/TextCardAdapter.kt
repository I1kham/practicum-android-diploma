package ru.practicum.android.diploma.presentation.card.text

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.practicum.android.diploma.databinding.TextItemBinding
import ru.practicum.android.diploma.domain.models.entity.Area

class TextCardAdapter : ListAdapter<Area, TextCardViewHolder>(TextCardDiffCallback()) {
    var onItemClick = { _: Area -> }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextCardViewHolder {
        val binding = TextItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TextCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TextCardViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClick.invoke(getItem(position)) }
    }
}
