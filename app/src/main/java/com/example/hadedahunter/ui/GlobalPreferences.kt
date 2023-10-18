package com.example.hadedahunter.ui

import androidx.lifecycle.ViewModel

class GlobalPreferences : ViewModel() {
    var SelectedMeasuringSystem: String = ""
    var MaximumDistance: Double = 0.0
}