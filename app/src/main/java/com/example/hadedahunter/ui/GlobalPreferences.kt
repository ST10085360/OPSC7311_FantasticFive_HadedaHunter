package com.example.hadedahunter.ui

import androidx.lifecycle.ViewModel

class GlobalPreferences : ViewModel() {
    var SelectedMeasuringSystem: String = "Kilometers"
    var MaximumDistance: Double = 50.0
}