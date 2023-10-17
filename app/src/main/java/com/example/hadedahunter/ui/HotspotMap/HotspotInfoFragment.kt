package com.example.hadedahunter.ui.HotspotMap

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.hadedahunter.R
import com.google.android.gms.maps.model.LatLng


class HotspotInfoFragment : DialogFragment() {
    private lateinit var context: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hotspot_info, container, false)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Retrieve information from arguments
        val hotspotName = arguments?.getString("hotspotName")
        val hotspotLocation = arguments?.getParcelable<LatLng>("hotspotLocation")
        val birdsObserved = arguments?.getStringArrayList("birdsObserved")

        // Set hotspot name and location
        val hotspotNameTextView = view.findViewById<TextView>(R.id.hotspot_name)
        hotspotNameTextView.text = hotspotName


        // Add observed birds dynamically
        val birdsObservedList = view.findViewById<LinearLayout>(R.id.birds_observed_list)
        birdsObserved?.forEach { bird ->
            val birdTextView = TextView(requireContext())
            birdTextView.text = bird
            birdTextView.textSize = 16f
            birdsObservedList.addView(birdTextView)
        }

        return view
    }
}
