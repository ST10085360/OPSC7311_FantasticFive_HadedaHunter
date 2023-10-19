package com.example.hadedahunter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CatalogueFragment : Fragment() {

    private lateinit var birdAdapter: CatalogueAdapter
    private lateinit var rvBirds: RecyclerView
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalogue, container, false)

        rvBirds = view.findViewById(R.id.rvBirds)
        rvBirds.layoutManager = LinearLayoutManager(context)

        birdAdapter = CatalogueAdapter(requireContext())
        rvBirds.adapter = birdAdapter

        searchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                birdAdapter.filter(newText ?: "")
                return true
            }
        })

        searchView.queryHint = "Search by common name"
        val allBirdsInDb = fetchBirdDataFromFirebase()
        birdAdapter.setData(allBirdsInDb)

        return view
    }

    private fun fetchBirdDataFromFirebase(): List<Bird> {
        val birds = mutableListOf<Bird>()
        val databaseReference = FirebaseDatabase.getInstance().reference.child("Birds")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (birdSnapshot in dataSnapshot.children) {
                    val birdName = birdSnapshot.key
                    val scientificName =
                        birdSnapshot.child("Scientific Name").getValue(String::class.java) ?: ""
                    val size = birdSnapshot.child("Size").getValue(String::class.java) ?: ""
                    val appearance = birdSnapshot.child("Appearance").getValue(String::class.java) ?: ""
                    val imageResource = R.drawable.bird_random

                    val bird = birdName?.let { Bird(it, scientificName, size, appearance, imageResource) }
                    if (bird != null) {
                        birds.add(bird)
                    }
                }
                birdAdapter.setData(birds)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FetchBirdData", "Error fetching data: ${databaseError.message}")
            }
        })

        return birds
    }
}