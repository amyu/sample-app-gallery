package com.example.gallery.domain

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryImageRepository
@Inject constructor(
    @ApplicationContext private val context: Context,
) {
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun findAll(): List<GalleryImage> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED,
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val contentResolver = context.contentResolver

        return@withContext contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            buildList {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    add(GalleryImage(id, contentUri))
                }
            }
        }
    } ?: emptyList()
}
