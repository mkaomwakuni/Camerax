package com.mkao.camerax.ui.gallery

import GalleryViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mkao.camerax.MainActivity
import com.mkao.camerax.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var viewModel:GalleryViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        // TODO: Apply the adapter to the RecyclerView
        viewModel = ViewModelProvider (this)[GalleryViewModel::class.java]
        viewModel.photos.observe(viewLifecycleOwner, {photos ->
            photos?.let {
            // TODO: Load the photo previews here }
           }
        })
        if (MainActivity.CameraPermissionHelper.hasStoragePermission(requireActivity()
            )) viewModel.loadPhotos()
        else
            MainActivity.CameraPermissionHelper.requestPermissions(requireActivity()) }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}