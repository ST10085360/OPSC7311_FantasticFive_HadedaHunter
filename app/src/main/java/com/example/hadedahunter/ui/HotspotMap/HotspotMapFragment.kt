package com.example.hadedahunter.ui.HotspotMap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.hadedahunter.R
import com.example.hadedahunter.ui.GlobalPreferences
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.PolyUtil
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class HotspotMapFragment : Fragment(), OnMapReadyCallback,
    AddObservationDialogFragment.OnObservationAddedListener {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLatitude: Double = 0.0
    private var userLongitude: Double = 0.0
    private var currentPolyline: Polyline? = null
    private val markerInfoMap = HashMap<Int, MarkerInfo>()
    private var uniqueId = 0
    private lateinit var preferences: GlobalPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hotspot_map, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()

        preferences = ViewModelProvider(requireActivity())[GlobalPreferences::class.java]

        // Access the properties
        val maxDistance = preferences.MaximumDistance.toString()
        val measuringSystem = preferences.SelectedMeasuringSystem.toString()



        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "")
        val userViewModel: UserViewModel by activityViewModels()
        if (userEmail != null) {
            userViewModel.userEmail = userEmail
        }


        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Check Google Play services availability
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())

        if (resultCode != ConnectionResult.SUCCESS) {
            googleApiAvailability.showErrorDialogFragment(requireActivity(), resultCode, 0)
            return null
        }

        val addObservationButton = view.findViewById<Button>(R.id.add_observation_button)
        addObservationButton.setOnClickListener {
            val dialogFragment = AddObservationDialogFragment()
            dialogFragment.setOnObservationAddedListener(this)
            dialogFragment.show(childFragmentManager, "AddObservationDialog")
            Log.d("Preferences distance", ": $maxDistance")
            Log.d("Preferences system", ": $measuringSystem")
        }

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true


            // Add markers from API
            Thread {
                try {
                    val url =
                        URL("https://api.ebird.org/v2/data/obs/geo/recent?lat=-34.0252729658478&lng=25.70184460498627&back=30&maxResults=100&includeProvisional=true&hotspot=true&sort=date&key=h0nip7qkvaqh")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    val responseCode = connection.responseCode

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val inputStream = connection.inputStream
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val response = StringBuilder()

                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }

                        reader.close()
                        inputStream.close()

                        val jsonArray = JSONArray(response.toString())
                        for (i in 0 until jsonArray.length()) {
                            val observation = jsonArray.getJSONObject(i)
                            val locName = observation.getString("locName")
                            val comName = observation.getString("comName")
                            val lat = observation.getDouble("lat")
                            val lng = observation.getDouble("lng")

                            val observationLocation = LatLng(lat, lng)

                            // Generate a unique ID
                            val markerId = generateUniqueId()

                            requireActivity().runOnUiThread {
                                // Add a marker to the map
                                val marker = googleMap.addMarker(
                                    MarkerOptions()
                                        .position(observationLocation)
                                        .title("Location: $locName")
                                )

                                // Calculate distance
                                val distance = calculateDistance(userLatitude, userLongitude, lat, lng)

                                // Check against max distance
                                if (distance > preferences.MaximumDistance) {
                                    marker?.remove()
                                }

                                // Store marker-specific information
                                val markerInfo = MarkerInfo(locName, comName)
                                markerInfoMap[markerId] = markerInfo

                                // Associate the marker with its ID
                                marker?.tag = markerId
                            }
                        }
                    } else {
                        // Handle HTTP error
                    }

                    connection.disconnect()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()

            readObservationsFromFirebase(googleMap)

            // Get the user's location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this@HotspotMapFragment.userLatitude = location.latitude
                    this@HotspotMapFragment.userLongitude = location.longitude
                    val latLng = LatLng(userLatitude, userLongitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                    // Set up marker click listener
                    googleMap.setOnMarkerClickListener { marker ->
                        currentPolyline?.remove() // Remove previous polyline

                        val destination = marker.position
                        var distance = calculateDistance(
                            userLatitude,
                            userLongitude,
                            destination.latitude,
                            destination.longitude
                        )

                        if (preferences.SelectedMeasuringSystem == "Miles") {
                            distance *= 0.621371
                        }

                        val measurementUnit = if (preferences.SelectedMeasuringSystem == "Miles") "miles" else "km"
                        val formattedDistance = "%.2f".format(distance).toDouble()

                        // Set title and snippet (distance) for the InfoWindow
                        marker.title = "Hotspot: ${marker.title}"
                        marker.snippet = "Distance: $formattedDistance $measurementUnit"
                        marker.showInfoWindow()

                        // Calculate the route
                        val directions = GeoApiContext.Builder()
                            .apiKey("AIzaSyAv3g9kdO8UbwiCx95YWF--6qDdaUE75Jg")
                            .build()

                        val request = DirectionsApi.getDirections(
                            directions,
                            "$userLatitude,$userLongitude",
                            "${destination.latitude},${destination.longitude}"
                        )

                        try {
                            val result = request.await()

                            if (result != null) {
                                val bestRoute = result.routes[0]
                                val steps = bestRoute.legs[0].steps

                                // Create a list of LatLng points for the route
                                val routePoints = ArrayList<LatLng>()
                                for (step in steps) {
                                    routePoints.addAll(PolyUtil.decode(step.polyline.encodedPath))
                                }

                                // Draw the route on the map
                                val polylineOptions = PolylineOptions()
                                    .addAll(routePoints)
                                    .color(Color.BLUE)
                                    .width(8f)
                                currentPolyline = googleMap.addPolyline(polylineOptions)
                            }
                        } catch (e: ApiException) {
                            e.printStackTrace()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        // Return true to indicate that the event is handled
                        true
                    }

                    googleMap.setOnInfoWindowClickListener { marker ->
                        val markerId = marker?.tag as Int
                        val markerInfo = markerInfoMap[markerId]

                        if (markerInfo != null) {
                            // Extract the information you want to pass to the new layout
                            val hotspotName = markerInfo.locName
                            val hotspotLocation = markerInfo.comName

                            // Create a list of birds observed (you'll need to modify this based on your data structure)
                            val birdsObserved = listOf(
                                markerInfo.comName,
                                // Add more observations as needed
                            )

                            // Create a new instance of HotspotInfoFragment
                            val hotspotInfoFragment = HotspotInfoFragment().apply {
                                arguments = Bundle().apply {
                                    putString("hotspotName", hotspotName)
                                    putStringArrayList("birdsObserved", ArrayList(birdsObserved))
                                }
                            }


                            //UPDATED METHOD
                            hotspotInfoFragment.show(parentFragmentManager, "HotspotInfoPopup")


                            //OLD METHOD

                            /*// Navigate to the new fragment
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.container, hotspotInfoFragment) // Adjust the container ID if needed
                                .addToBackStack(null)
                                .commit()*/
                        }
                        true
                    }

                }
            }
        }
    }


    override fun onObservationAdded(locName: String, comName: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "")
        val userViewModel: UserViewModel by activityViewModels()
        if (userEmail != null) {
            userViewModel.userEmail = userEmail
        }

        val encodedEmail = userEmail?.replace(".", ",")
        val observation = Observation(locName, comName, userLatitude, userLongitude, encodedEmail)

        // Add the observation to Firebase
        val database = FirebaseDatabase.getInstance()
        val userObservationsRef = database.getReference("Observations/$encodedEmail")
        userObservationsRef.push().setValue(observation)
    }

    private fun showAddObservationDialog() {
        val dialog = AddObservationDialogFragment()
        dialog.setOnObservationAddedListener(this)
        dialog.show(parentFragmentManager, "AddObservationDialog")
    }


    private fun generateUniqueId(): Int {
        uniqueId++
        return uniqueId
    }

    private fun readObservationsFromFirebase(googleMap: GoogleMap) {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "")
        val userViewModel: UserViewModel by activityViewModels()
        if (userEmail != null) {
            userViewModel.userEmail = userEmail
        }

        val encodedEmail = userEmail?.replace(".", ",")

        val database = FirebaseDatabase.getInstance()
        val userObservationsRef = database.getReference("Observations/$encodedEmail")

        userObservationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val observation = childSnapshot.getValue(Observation::class.java)
                    observation?.let {
                        val observationLocation = LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0)

                        val marker = MarkerOptions()
                            .position(observationLocation)
                            .title("Location: ${it.locName}")

                        // Store marker-specific information
                        val markerInfo = MarkerInfo(it.locName ?: "", it.comName ?: "")
                        val markerId = generateUniqueId()
                        markerInfoMap[markerId] = markerInfo

                        // Associate the marker with its ID
                        val addedMarker = googleMap.addMarker(marker)
                        if (addedMarker != null) {
                            addedMarker.tag = markerId
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun calculateDistance(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Double {
        val earthRadius = 6371 // Radius of the earth in km
        val dLat = Math.toRadians(endLatitude - startLatitude)
        val dLng = Math.toRadians(endLongitude - startLongitude)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(startLatitude)) * Math.cos(Math.toRadians(endLatitude)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
