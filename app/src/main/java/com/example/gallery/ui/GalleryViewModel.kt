package com.example.gallery.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.domain.GalleryImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel
@Inject constructor(
    application: Application,
    private val galleryImageRepository: GalleryImageRepository
) : AndroidViewModel(application) {
    data class UIState(
        val galleryItems: List<GalleryItemViewHolder.Item> = emptyList(),
    )

    val uiState = MutableStateFlow(UIState())

    fun onCreateAfterPermissionGranted() {
        viewModelScope.launch {
            val galleryImages = galleryImageRepository.findAll()

            val newStatus = uiState.value.copy(galleryItems = galleryImages.map {
                GalleryItemViewHolder.Item(
                    id = it.id,
                    contentUri = it.contentUri,
                    isSelected = false,
                )
            })
            uiState.emit(newStatus)
        }
    }

    fun onClickItem(item: GalleryItemViewHolder.Item) {
        viewModelScope.launch {
            uiState.value.galleryItems.map { it.copy(isSelected = it.id == item.id) }
                .let {
                    uiState.emit(uiState.value.copy(galleryItems = it))
                }
        }
    }
}