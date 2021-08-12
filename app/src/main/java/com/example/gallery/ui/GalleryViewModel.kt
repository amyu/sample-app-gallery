package com.example.gallery.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gallery.domain.GalleryImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

            galleryImages.chunked(10).forEach {
                val newItems = it.map {
                    GalleryItemViewHolder.Item(
                        id = it.id,
                        contentUri = it.contentUri,
                        isSelected = false,
                        thumbnailBitmap = createThumbnailBitmap(
                            getApplication(),
                            it.id,
                            it.contentUri
                        )
                    )
                }

                val newStatus =
                    uiState.value.copy(galleryItems = uiState.value.galleryItems + newItems)
                uiState.emit(newStatus)
            }
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

    private suspend fun createThumbnailBitmap(
        context: Context,
        id: Long,
        contentUri: Uri,
    ): Bitmap = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            @Suppress("BlockingMethodInNonBlockingContext", "IOで実行している")
            context.contentResolver.loadThumbnail(contentUri, Size(200, 200), null)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Thumbnails.getThumbnail(
                context.contentResolver,
                id,
                MediaStore.Images.Thumbnails.MINI_KIND,
                null
            )
        }
    }
}