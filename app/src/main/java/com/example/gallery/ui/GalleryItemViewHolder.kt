package com.example.gallery.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.gallery.R
import com.example.gallery.databinding.ItemGalleryBinding

class GalleryItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_gallery, parent, false)
) {
    fun bind(
        item: Item,
        onClickItem: (Item) -> Unit
    ) {
        val binding = ItemGalleryBinding.bind(itemView)
        binding.root.setOnClickListener { onClickItem(item) }
        binding.imageView.setImageURI(item.contentUri)
        binding.selectedView.isVisible = item.isSelected
    }

    data class Item(
        val id: Long,
        val contentUri: Uri,
        val isSelected: Boolean,
    )
}
