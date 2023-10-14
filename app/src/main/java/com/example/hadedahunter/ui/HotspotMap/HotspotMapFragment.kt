package com.example.hadedahunter.ui.HotspotMap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hadedahunter.R
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
import java.io.IOException
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.google.maps.model.Unit

class HotspotMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLatitude: Double = 0.0
    private var userLongitude: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hotspot_map, container, false)

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

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true

            // Add markers here
            val birdWatchingLocation1 = LatLng(-34.02527296584781, 25.70184460498627)
            val birdWatchingLocation2 = LatLng(-34.00652418250723, 25.68701224180276)
            val birdWatchingLocation3 = LatLng(-34.01052017217004, 25.36885805103418)
            val birdWatchingLocation4 = LatLng(-33.97885448322565, 25.36737481471583)
            val birdWatchingLocation5 = LatLng(-33.97599584324513, 25.462583545778575)
            val birdWatchingLocation6 = LatLng(-33.95136504668145, 25.556310656925213)
            val birdWatchingLocation7 = LatLng(-33.966457594930795, 25.60265922882768)
            val birdWatchingLocation8 = LatLng(-33.95019135988834, 25.044963366121028)
            val birdWatchingLocation9 = LatLng(-33.96828855348131, 25.578997313479825)

            // Add markers to the map
            googleMap.addMarker(MarkerOptions().position(birdWatchingLocation1).title("Cape Recife Lighthouse"))
            googleMap.addMarker(MarkerOptions().position(birdWatchingLocation2).title("SANCCOB Gqeberha"))
            googleMap.addMarker(MarkerOptions().position(birdWatchingLocation3).title("Alan Tours"))
            googleMap.addMarker(MarkerOptions().position(birdWatchingLocation4).title("The Island Nature Reserve"))
            googleMap.addMarker(MarkerOptions().position(birdWatchingLocation5).title("Kragga Kamma Game Park"))
            googleMap.addMarker(MarkerOptions().position(birdWatchingLocation6).title("Newton Park Nature Reserve"))
            googleMap.addMarker(MarkerOptions().position(birdWatchingLocation7).title("Settlers Park Nature Reserve"))
            googleMap.addMarker(MarkerOptions().position(birdWatchingLocation8).title("SloeJoe Birding"))
            googleMap.addMarker(MarkerOptions().position(birdWatchingLocation9).title("Dodd's Farm"))

            // Get the user's location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this@HotspotMapFragment.userLatitude = location.latitude
                    this@HotspotMapFragment.userLongitude = location.longitude
                    val latLng = LatLng(userLatitude, userLongitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                    // Set up marker click listener
                    googleMap.setOnMarkerClickListener { marker ->
                        // Get the destination's latitude and longitude
                        val destination = marker.position

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

                            // Process result to get the best route and display it to the user
                            if (result != null) {
                                val bestRoute = result.routes[0]
                                val steps = bestRoute.legs[0].steps

                                // You can use 'steps' to get detailed steps of the route
                            }
                        } catch (e: ApiException) {
                            e.printStackTrace()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        true // Return true to indicate that the event is handled
                    }
                }
            }
        }
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
