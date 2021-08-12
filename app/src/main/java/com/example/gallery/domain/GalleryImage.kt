package com.example.gallery.domain

import android.net.Uri

data class GalleryImage(
    val id: Long,
    val contentUri: Uri,
)
