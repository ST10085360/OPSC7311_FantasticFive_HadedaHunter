package com.example.hadedahunter.ui.HotspotMap

class Observation {
    var locName: String? = null
    var comName: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var userEmail: String? = null
    var imageUrl: String? = null

    // Default constructor required for Firebase
    constructor()

    constructor(locName: String?, comName: String?, latitude: Double?, longitude: Double?, userEmail: String?, imageUrl: String?) {
        this.locName = locName
        this.comName = comName
        this.latitude = latitude
        this.longitude = longitude
        this.userEmail = userEmail
        this.imageUrl = imageUrl
    }
}
