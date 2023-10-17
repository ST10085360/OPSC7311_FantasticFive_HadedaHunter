package com.example.hadedahunter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView


//Adapter for the Catalogue Card View
class CatalogueAdapter(private val context: Context, private val birds: List<Bird>) : RecyclerView.Adapter<CatalogueAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val birdNameTextView: TextView = itemView.findViewById(R.id.birdNameTextView)
        val birdImageView: ImageView = itemView.findViewById(R.id.birdImageView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val bird = birds[position]
                    showBirdDetailsDialog(bird)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.catalogue_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bird = birds[position]

        // Bind the data to the views
        holder.birdNameTextView.text = bird.name
        //holder.scientificNameTextView.text = bird.scientificName
        //holder.appearanceTextView.text = bird.appearance
        //holder.sizeTextView.text = bird.size

        // Set the placeholder image
        holder.birdImageView.setImageResource(bird.imageResource)
    }

    private fun showBirdDetailsDialog(bird: Bird) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.bird_details_dialog)

        val birdNameTextView = dialog.findViewById<TextView>(R.id.birdName)
        val scientificNameTextView = dialog.findViewById<TextView>(R.id.scientificName)
        val sizeTextView = dialog.findViewById<TextView>(R.id.size)
        val appearanceTextView = dialog.findViewById<TextView>(R.id.appearance)

        birdNameTextView.text = bird.name
        scientificNameTextView.text = "Scientific Name: " + bird.scientificName
        sizeTextView.text = "Size: " + bird.size
        appearanceTextView.text = "Appearance: " + bird.appearance

        val closeButton = dialog.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    override fun getItemCount() = birds.size
}

