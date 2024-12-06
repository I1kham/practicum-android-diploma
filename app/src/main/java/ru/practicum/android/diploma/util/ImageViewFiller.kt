package ru.practicum.android.diploma.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ViewTarget
import ru.practicum.android.diploma.R

fun ImageView.fillBy(
    uri: String?,
    context: Context,
): ViewTarget<ImageView, Drawable> {
    return Glide.with(context)
        .load(uri)
        .placeholder(R.drawable.placeholder_recycleview)
        .fitCenter()
        .into(this)
}
