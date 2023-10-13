package com.example.hadedahunter.ui.home

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hadedahunter.databinding.FragmentHomeBinding
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = binding.root

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
                val currentTemp = jsonObject.getJSONObject("currentConditions").getDouble("temp")
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


                // Update UI elements
                Handler(Looper.getMainLooper()).post {
                    binding.txtLocation.text = shortCityName
                    binding.txtTemp.text = "${currentTempInt}°C"
                    binding.txtHighTemp.text = "${highTempInt}°/${lowTempInt}°"
                    binding.txtCondition.text = description
                    binding.txtCurrentDate.text = currentDate

                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                urlConnection.disconnect()
            }


        }.start()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
