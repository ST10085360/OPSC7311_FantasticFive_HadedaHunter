package com.example.hadedahunter.ui.HotspotMap

import androidx.lifecycle.ViewModel
import com.example.hadedahunter.ui.UserPreferences

class UserViewModel : ViewModel() {
    var userEmail: String = ""
    var userPreferences: UserPreferences? = null
    // Add other user-related properties if needed
}