package com.example.hadedahunter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


//Adapter for the Catalogue Card View
class CatalogueAdapter(private val context: Context) : RecyclerView.Adapter<CatalogueAdapter.ViewHolder>() {

    private val birds = ArrayList<Bird>()
    private val filteredBirds = ArrayList<Bird>()
    private var hasResults: Boolean = true

    init {
        filteredBirds.addAll(birds)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val birdNameTextView: TextView = itemView.findViewById(R.id.birdNameTextView)
        val birdImageView: ImageView = itemView.findViewById(R.id.birdImageView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val bird = filteredBirds[position]
                    showBirdDetailsDialog(bird)
                }
            }
        }
    }

    fun setData(birds: List<Bird>) {
        this.birds.clear()
        this.birds.addAll(birds)
        filter("")
    }

    fun filter(query: String) {
        filteredBirds.clear()

        if (query.isEmpty()) {
            filteredBirds.addAll(birds) // Reset to all birds if the query is empty
        } else {
            val lowercaseQuery = query.toLowerCase()
            for (bird in birds) {
                if (bird.name.toLowerCase().contains(lowercaseQuery)) {
                    filteredBirds.add(bird)
                }
            }
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.catalogue_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bird = filteredBirds.getOrNull(position)

        if (bird != null) {
            holder.birdNameTextView.text = bird.name

            // Load image using Glide
            Glide.with(context)
                .load(bird.imageUrl) // Add a new property imageUrl to your Bird class
                .centerCrop()
                .into(holder.birdImageView)
        } else {
            Toast.makeText(context, "No results found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBirdDetailsDialog(bird: Bird) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.bird_details_dialog)

        val birdNameTextView = dialog.findViewById<TextView>(R.id.birdName)
        val scientificNameTextView = dialog.findViewById<TextView>(R.id.scientificName)
        val sizeTextView = dialog.findViewById<TextView>(R.id.size)
        val appearanceTextView = dialog.findViewById<TextView>(R.id.appearance)

        birdNameTextView.text = bird.name
        scientificNameTextView.text = "Scientific name:  " + bird.scientificName
        sizeTextView.text = "Size:  " + bird.size
        appearanceTextView.text = "Appearance:  " + bird.appearance

        val closeButton = dialog.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getItemCount() = filteredBirds.size
}
