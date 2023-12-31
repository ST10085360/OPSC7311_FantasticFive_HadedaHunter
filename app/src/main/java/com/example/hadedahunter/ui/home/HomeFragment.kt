package com.example.hadedahunter.ui.home

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hadedahunter.ObservationAdapter
import com.example.hadedahunter.R
import com.example.hadedahunter.databinding.FragmentHomeBinding
import com.example.hadedahunter.ui.HotspotMap.Observation
import com.example.hadedahunter.ui.HotspotMap.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var observationAdapter: ObservationAdapter
    private val binding get() = _binding!!
    private var currentTemp = 0.0;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

        (activity as AppCompatActivity).supportActionBar?.hide()


        // Retrieve the user's name from the Intent extras
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "")
        val userViewModel: UserViewModel by activityViewModels()
        if (userName != null) {
            userViewModel.userEmail = userName
        }

        // Getting the current time
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)

        // Updating the greeting text based on the current time
        val greeting = when {
            currentHour in 0..11 -> "Good Morning, "
            currentHour in 12..16 -> "Good Afternoon, "
            else -> "Good Evening, "
        }
        binding.txtGreeting.text = greeting

        // Make API request
        Thread {
            val url = URL("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Port%20Elizabeth/today?unitGroup=metric&include=current%2Cfcst&key=B5BW4SKEUJBJ27TLX8WXFHP9M&options=nonulls&contentType=json")
            val urlConnection = url.openConnection() as HttpURLConnection

            try {
                val inputStream = urlConnection.inputStream
                val reader = InputStreamReader(inputStream)
                val result = reader.readText()

                // Parse JSON response
                val jsonObject = JSONObject(result)
                val cityName = jsonObject.getString("resolvedAddress")
                currentTemp = jsonObject.getJSONObject("currentConditions").getDouble("temp")
                val highTemp = jsonObject.getJSONArray("days")
                    .getJSONObject(0).getDouble("tempmax")
                val lowTemp = jsonObject.getJSONArray("days")
                    .getJSONObject(0).getDouble("tempmin")
                val description = jsonObject.getJSONArray("days")
                    .getJSONObject(0).getString("description")

                val currentTempInt = currentTemp.toInt()
                val highTempInt = highTemp.toInt()
                val lowTempInt = lowTemp.toInt()
                val resolvedAddress = jsonObject.getString("resolvedAddress")
                val shortCityName = resolvedAddress.split(",")[0].trim()
                val currentDate = SimpleDateFormat("EEE, dd MMMM", Locale.getDefault()).format(Date())
                val birdWatchingPercentage = calculateBirdWatchingConditions(currentTempInt)


                // Update UI elements
                Handler(Looper.getMainLooper()).post {
                    binding.txtLocation.text = shortCityName
                    binding.txtTemp.text = "${currentTempInt}°C"
                    binding.txtHighLow.text = "${highTempInt}°/${lowTempInt}°"
                    binding.txtCondition.text = description
                    binding.txtCurrentDate.text = currentDate
                    binding.txtWatchingCondition2.text = "${birdWatchingPercentage.toString()}%"
                    binding.txtName.text = userName
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                urlConnection.disconnect()
            }


        }.start()

        // Initialize RecyclerView and ObservationAdapter
        val observationsRecyclerView = root.findViewById<RecyclerView>(R.id.observationsRecyclerView)
        observationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        observationAdapter = ObservationAdapter(emptyList()) // Initialize with an empty list
        observationsRecyclerView.adapter = observationAdapter

        // Fetch observations from Firebase
        readObservationsFromFirebase()

        // Initialize "Back to Top" button
        val btnBackToTop = root.findViewById<ImageButton>(R.id.btnBackToTop)
        btnBackToTop.setOnClickListener {
            // Scroll to the top of the NestedScrollView when the button is clicked
            binding.nestedScrollView.smoothScrollTo(0, 0)
        }

        return root
    }

    private fun readObservationsFromFirebase() {
        val userViewModel: UserViewModel by activityViewModels()
        val userEmail = Firebase.auth.currentUser?.email.toString()
        userEmail?.let { userViewModel.userEmail = it }

        if (userEmail != null) {
            val encodedEmail = userEmail.replace(".", ",")

            val database = FirebaseDatabase.getInstance()
            val userObservationsRef = database.getReference("Observations/$encodedEmail")

            Log.d("EncodedEmail", "Email: $encodedEmail")
            userObservationsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d("ObservationData", "Number of children: ${snapshot.childrenCount}")

                    val observations = mutableListOf<Observation>()

                    for (childSnapshot in snapshot.children) {
                        val observation = childSnapshot.getValue(Observation::class.java)
                        observation?.let {
                            observations.add(it)
                        }
                    }

                    // Log observations
                    Log.d("Observations", observations.toString())

                    // Update the ObservationAdapter with the new data
                    observationAdapter.updateData(observations)

                    Log.d("ObservationData", "Observations: $observations")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Error fetching observations: ${error.message}")
                }

            })
        }
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun calculateBirdWatchingConditions(currentTempInt: Int): Int {
        val perfectConditionsRange = 22..25
        val perfectConditionsPercentage = 100

        val birdWatchingPercentage = when {
            currentTempInt in perfectConditionsRange -> perfectConditionsPercentage
            currentTempInt < perfectConditionsRange.first -> {
                val degreesBelow = perfectConditionsRange.first - currentTempInt
                perfectConditionsPercentage - degreesBelow * 5
            }
            currentTempInt > perfectConditionsRange.last -> {
                val degreesAbove = currentTempInt - perfectConditionsRange.last
                perfectConditionsPercentage - degreesAbove * 5
            }
            else -> 0 // For temperatures outside the specified range
        }

        return birdWatchingPercentage
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
