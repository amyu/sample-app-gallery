package com.example.gallery.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.gallery.R
import com.example.gallery.databinding.FragmentGalleryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {
    companion object {
        fun newInstance(): GalleryFragment = GalleryFragment().apply {
            arguments = bundleOf()
        }
    }

    private val viewModel: GalleryViewModel by viewModels()

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) { isGranted ->
        val activity = activity ?: return@registerForActivityResult

        if (isGranted) {
            viewModel.onCreateAfterPermissionGranted()
            return@registerForActivityResult
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showDeniedReadExternalPermission()
        } else {
            showNeverAskForCallPermissions()
        }
    }

    private fun showDeniedReadExternalPermission() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("この機能を使うためには写真へのアクセスが必要です")
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ -> requestPermission.launch(Unit) }
            .show()
    }

    private fun showNeverAskForCallPermissions() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("この機能を使うために、設定画面>許可からご自身で権限を許可していただく必要があります")
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> requireActivity().finish() }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // この画面はPermissionを取得しない限り何も行わせない
        // そのためこの処理移行にコードを書くことを禁ず
        requestPermission.launch(Unit)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentGalleryBinding.bind(view)

        val adapter = GalleryAdapter(viewModel::onClickItem)
        binding.galleryRecyclerView.adapter = adapter
        binding.galleryRecyclerView.setHasFixedSize(true)
        binding.galleryRecyclerView.itemAnimator = null
        binding.galleryRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.HORIZONTAL
            ).apply {
                drawable?.setTint(ContextCompat.getColor(requireContext(), R.color.white))
            }
        )
        binding.galleryRecyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            ).apply {
                drawable?.setTint(ContextCompat.getColor(requireContext(), R.color.white))
            }
        )

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                adapter.submitList(it.galleryItems)
            }
        }
    }
}