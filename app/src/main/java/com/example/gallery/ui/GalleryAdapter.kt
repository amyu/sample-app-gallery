package com.example.gallery.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class GalleryAdapter(
    private val onClickItem: (GalleryItemViewHolder.Item) -> Unit
) : ListAdapter<GalleryItemViewHolder.Item, GalleryItemViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<GalleryItemViewHolder.Item>() {
            override fun areItemsTheSame(
                oldItem: GalleryItemViewHolder.Item,
                newItem: GalleryItemViewHolder.Item
            ): Boolean = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: GalleryItemViewHolder.Item,
                newItem: GalleryItemViewHolder.Item
            ): Boolean = oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GalleryItemViewHolder = GalleryItemViewHolder(parent)

    override fun onBindViewHolder(holder: GalleryItemViewHolder, position: Int) {
        holder.bind(
            item = getItem(position),
            onClickItem = onClickItem
        )
    }
}

