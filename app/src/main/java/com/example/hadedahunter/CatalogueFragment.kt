package com.example.hadedahunter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CatalogueFragment : Fragment() {

    private lateinit var birdAdapter: CatalogueAdapter
    private lateinit var rvBirds: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalogue, container, false)

        rvBirds = view.findViewById(R.id.rvBirds)
        rvBirds.layoutManager = LinearLayoutManager(context)

        // Fetch bird data from Firebase (replace with your own code)
        // Example:
        val allBirdsInDb = fetchBirdDataFromFirebase()

        birdAdapter = CatalogueAdapter(requireContext(), allBirdsInDb)
        rvBirds.adapter = birdAdapter

        return view
    }

    // Replace this with your actual data retrieval logic from Firebase
    private fun fetchBirdDataFromFirebase(): List<Bird> {
        val birds = mutableListOf<Bird>()

        // Replace with your Firebase database reference
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Birds")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (birdSnapshot in dataSnapshot.children) {
                    val birdName = birdSnapshot.key
                    val scientificName =
                        birdSnapshot.child("Scientific Name").getValue(String::class.java) ?: ""
                    val size = birdSnapshot.child("Size").getValue(String::class.java) ?: ""
                    val appearance = birdSnapshot.child("Appearance").getValue(String::class.java) ?: ""

                    // Use a placeholder image resource ID
                    val imageResource = R.drawable.bird_random

                    val bird = birdName?.let { Bird(it, scientificName, size, appearance, imageResource) }
                    if (bird != null) {
                        birds.add(bird)
                    }
                }

                // Update the RecyclerView with the retrieved data
                birdAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("FetchBirdData", "Error fetching data: ${databaseError.message}")
            }
        })

        return birds
    }
}
