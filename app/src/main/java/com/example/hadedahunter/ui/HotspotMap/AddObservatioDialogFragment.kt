package com.example.hadedahunter.ui.HotspotMap

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.hadedahunter.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddObservationDialogFragment : DialogFragment() {

    interface OnObservationAddedListener {
        fun onObservationAdded(locName: String, comName: String)
    }

    private var listener: OnObservationAddedListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.add_observation_popup, null)

        val locNameInput = view.findViewById<EditText>(R.id.location_name_edit_text)
        val comNameInput = view.findViewById<EditText>(R.id.common_bird_name_edit_text)

        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(view)
            .setTitle("Add Observation")
            .setPositiveButton("Add") { _, _ ->
                val locName = locNameInput.text.toString()
                val comName = comNameInput.text.toString()

                if (locName.isNotEmpty() && comName.isNotEmpty()) {
                    listener?.onObservationAdded(locName, comName)
                }
            }
            .setNegativeButton("Cancel", null)

        return builder.create()
    }

    fun setOnObservationAddedListener(listener: OnObservationAddedListener) {
        this.listener = listener
    }
}