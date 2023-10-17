package com.example.hadedahunter.ui.HotspotMap

data class Observation(
    var locName: String? = null,
    var comName: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
) {
    // Add a default (no-argument) constructor here
    constructor() : this(null, null, null, null)
}