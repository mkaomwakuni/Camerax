package com.mkao.camerax.ui.gallery

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.mkao.camerax.MainActivity
import com.mkao.camerax.Photo
import com.mkao.camerax.R
import com.mkao.camerax.databinding.FragmentPhotoFilterBinding
import jp.wasabeef.glide.transformations.GrayscaleTransformation
import jp.wasabeef.glide.transformations.gpu.*

class PhotoFilterFragment : Fragment() {

    private var _binding: FragmentPhotoFilterBinding? = null
    private val binding get() = _binding!!
    private var photo: Photo? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            val safeArgs = PhotoFilterFragmentArgs.fromBundle(it)
            photo = safeArgs.photo
        }

        _binding = FragmentPhotoFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        loadImage(null)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.filters_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.filterSpinner.adapter = adapter
        }

        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val filter = parent?.getItemAtPosition(position).toString()
                applyFilter(filter)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.save).isVisible = true

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
            R.id.save -> {
                val image = getBitmapFromView(binding.selectedImage)
                if (image != null) (activity as MainActivity).saveImage(image)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun applyFilter(filter: String?) {
        when (filter) {
            "None" -> loadImage(null)
            "Greyscale" -> loadImage(GrayscaleTransformation())
            "Swirl" -> loadImage(SwirlFilterTransformation(0.5f, 1.0f, PointF(0.5f, 0.5f)))
            "Invert filter" -> loadImage(InvertFilterTransformation())
            "Kuwahara filter" -> loadImage(KuwaharaFilterTransformation(25))
            "Sketch filter" -> loadImage(SketchFilterTransformation())
            "Toon filter" -> loadImage(ToonFilterTransformation())
        }
    }

    private fun loadImage(glideFilter: Transformation<Bitmap>?){
        when {
            photo != null && glideFilter != null -> {
                Glide.with(this)
                    .load(photo!!.uri)
                    .transform(
                        CenterCrop(),
                        glideFilter
                    )
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.selectedImage)
            }
            photo != null -> {
                Glide.with(this)
                    .load(photo!!.uri)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.selectedImage)
            }
        }
    }

    private fun getBitmapFromView(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
