package com.example.hadedahunter.ui.HotspotMap

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.hadedahunter.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddObservationDialogFragment : DialogFragment() {

    interface OnObservationAddedListener {
        fun onObservationAdded(locName: String, comName: String, imageBitmap: Bitmap?)

    }

    private var listener: OnObservationAddedListener? = null
    private var birdImageView: ImageView? = null
    private var capturedImageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_observation_popup, container, false)

        birdImageView = view.findViewById(R.id.bird_image_image_view)

        // Make the ImageView clickable
        birdImageView?.setOnClickListener {
            showImageSourceOptions()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locNameInput = view.findViewById<EditText>(R.id.location_name_edit_text)
        val comNameInput = view.findViewById<EditText>(R.id.common_bird_name_edit_text)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnAdd = view.findViewById<Button>(R.id.btnAdd)

        btnCancel.setOnClickListener {
            dismiss() // Close the dialog
        }

        btnAdd.setOnClickListener {
            val locName = locNameInput.text.toString()
            val comName = comNameInput.text.toString()

            if (locName.isNotEmpty() && comName.isNotEmpty()) {
                listener?.onObservationAdded(locName, comName, capturedImageBitmap)
                dismiss() // Close the dialog
            }
        }

        dialog?.setOnDismissListener {
        }
    }


    private fun showImageSourceOptions() {
        val options = arrayOf("Take a Picture", "Choose from Gallery")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera() // Take a Picture
                    1 -> uploadPicture() // Choose from Gallery
                }
            }
            .show()
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun uploadPicture() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_IMAGE_UPLOAD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Handle the result of image capture
                    capturedImageBitmap = data?.extras?.get("data") as? Bitmap
                    birdImageView?.setImageBitmap(capturedImageBitmap)
                }
                REQUEST_IMAGE_UPLOAD -> {
                    // Handle the result of image upload from the gallery
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        loadImageFromUri(selectedImageUri)
                    }
                }
            }
        }
    }

    private fun loadImageFromUri(uri: Uri) {
        Glide.with(requireContext())
            .load(uri)
            .into(birdImageView!!)
    }

    fun setOnObservationAddedListener(listener: OnObservationAddedListener) {
        this.listener = listener
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 101
        private const val REQUEST_IMAGE_UPLOAD = 102
    }
}