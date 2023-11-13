package com.example.hadedahunter.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GlobalPreferences : ViewModel() {
    var SelectedMeasuringSystem: String = "Kilometers"
    var MaximumDistance: Double = 50.0
    private val database = FirebaseDatabase.getInstance()

    fun updateUserPreferences(userEmail: String, preferences: UserPreferences) {
        val encodedEmail = userEmail?.replace(".", ",")
        val userPreferencesRef = database.getReference("UserPreferences/$encodedEmail")
        userPreferencesRef.setValue(preferences)
    }

    fun fetchUserPreferences(userEmail: String, callback: (UserPreferences?) -> Unit) {
        val encodedEmail = userEmail.replace(".", ",")
        val userPreferencesRef = database.getReference("UserPreferences/$encodedEmail")

        userPreferencesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val preferences = snapshot.getValue(UserPreferences::class.java)
                callback(preferences)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
}