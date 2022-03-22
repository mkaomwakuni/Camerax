package com.mkao.camerax.ui.camera

import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.mkao.camerax.MainActivity
import com.mkao.camerax.R
import com.mkao.camerax.databinding.FragementCameraBinding




class cameraFragment : Fragment() {
    private var _binding: FragementCameraBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {_binding = FragementCameraBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor= Executors.newSingleThreadExecutor()
        openCamera()

        //capture requests when the floating action button is clicked
        binding.fabTakePhoto.setOnClickListener {
            capturePhoto()
        }
    }

    private fun openCamera() {
        if
                (MainActivity.CameraPermissionHelper.hasCameraPermission(requireActivity()
            ) &&
            MainActivity.CameraPermissionHelper.hasStoragePermission(requireActivity())
        ) {
            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireActivity())
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder() .build()
                    .also {
                        it.setSurfaceProvider(binding.cameraFeed.surfaceProvider) }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                // Initialise the ImageCapture instance here
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                try {
                cameraProvider.unbindAll()
                    //bind image instance to camera state
                cameraProvider.bindToLifecycle(this, cameraSelector, preview,imageCapture) }
                catch (e: IllegalStateException) {
                Toast.makeText(requireActivity(),
                    resources.getString(R.string.error_connecting_camera),
                    Toast.LENGTH_LONG).show() }
            }, ContextCompat.getMainExecutor(requireActivity()))
                } else
            // Initialise the ImageCapture instance here

        {
            MainActivity.CameraPermissionHelper.requestPermissions(requireActivity())
        }
    }

    private fun capturePhoto() {
        //This capturePhoto method will capture a freeze-frame of the camera
        //feed and save the resulting image to the user’s device
        if (!this::imageCapture.isInitialized) {
            Toast.makeText(requireActivity(),
                resources.getString(R.string.error_saving_photo),
                Toast.LENGTH_LONG).show()
            return }

        val contentValues = (activity as MainActivity).prepareContentValues()

        val outputFileOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().applicationContext.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()

        imageCapture.takePicture(
            outputFileOptions, ContextCompat.getMainExecutor(requireActivity()),
            object : ImageCapture.OnImageSavedCallback {

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(requireActivity(),
                        resources.getString(R.string.error_saving_photo),
                        Toast.LENGTH_LONG).show() }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(requireActivity(),
                        resources.getString(R.string.photo_saved), Toast.LENGTH_LONG).show() }
            })
    }
}