package com.example.hadedahunter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hadedahunter.ui.HotspotMap.Observation

class ObservationAdapter(private var observations: List<Observation>) :
    RecyclerView.Adapter<ObservationAdapter.ObservationViewHolder>() {

    inner class ObservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val observationImage: ImageView = itemView.findViewById(R.id.observationImage)
        val locationTextView: TextView = itemView.findViewById(R.id.observationLocation)
        val commonNameTextView: TextView = itemView.findViewById(R.id.observationCommon)
        // Add more TextViews or other views as needed for additional observation details
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_observation, parent, false)
        return ObservationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ObservationViewHolder, position: Int) {
        val observation = observations[position]

        Glide.with(holder.itemView)
            .load(observation.imageUrl)
            .placeholder(R.drawable.bird_random)
            .into(holder.observationImage)

        // Set other data to TextViews
        holder.locationTextView.text = observation.locName
        holder.commonNameTextView.text = observation.comName
    }

    fun updateData(newObservations: List<Observation>) {
        observations = newObservations
        notifyDataSetChanged()

        Log.d("Adapter", "Item count: ${itemCount}")
    }

    override fun getItemCount(): Int {
        return observations.size
    }
}