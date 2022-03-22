package com.mkao.camerax.ui.gallery

import GalleryViewModel
import com.mkao.camerax.Photo
import android.app.RecoverableSecurityException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mkao.camerax.MainActivity
import com.mkao.camerax.R
import com.mkao.camerax.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var viewModel:GalleryViewModel
    private lateinit var galleryAdapter: GalleryAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val registerResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
        // Result code of 0 means the user declined permission to delete the photo
        if (result.resultCode != 0) deletePhoto()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        galleryAdapter =GalleryAdapter(activity as MainActivity, this)
        binding.root.layoutManager = GridLayoutManager(context, 3)
        binding.root.adapter = galleryAdapter

        viewModel = ViewModelProvider (this)[GalleryViewModel::class.java]
        viewModel.photos.observe(viewLifecycleOwner, {photos ->
            photos?.let {

                galleryAdapter.notifyItemRangeRemoved(0, galleryAdapter.itemCount)
                galleryAdapter.photos = it
                galleryAdapter.notifyItemRangeInserted(0, it.size)
           }
        })
        if (MainActivity.CameraPermissionHelper.hasStoragePermission(requireActivity()
            )) viewModel.loadPhotos()
        else
            MainActivity.CameraPermissionHelper.requestPermissions(requireActivity()) }

    fun showPopup(view: View, photo: Photo) {
        val popup = PopupMenu(requireActivity(), view)
        popup.inflate(R.menu.popup)
        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.popup_delete) {
                viewModel.photoToDelete = photo
                deletePhoto()
            }
            true }
        popup.show()
    }

    private fun deletePhoto() {
        try {
            val photo = viewModel.photoToDelete ?: return
            val rowsDeleted = requireActivity().applicationContext.contentResolver.delete(photo.uri, null)
            if(rowsDeleted == 1) viewModel.photoToDelete = null }
        catch
            (recoverableSecurityException: RecoverableSecurityException) {
            val intentSender = recoverableSecurityException.userAction.actionIntent.intentSender
            val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
            registerResult.launch(intentSenderRequest) }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}