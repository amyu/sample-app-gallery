package com.example.gallery.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gallery.R
import com.example.gallery.databinding.ActivityGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val binding = ActivityGalleryBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.rootContainer.id,
                    GalleryFragment.newInstance()
                )
                .commitNow()
        }
    }
}