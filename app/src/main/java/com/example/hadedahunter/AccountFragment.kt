package com.example.hadedahunter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.hadedahunter.startup.SplashScreen
import com.example.hadedahunter.ui.GlobalPreferences
import com.example.hadedahunter.ui.HotspotMap.UserViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
@Suppress("UNREACHABLE_CODE")
class AccountFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var logoutButton: Button
    private lateinit var measuringSystemSpinner: Spinner
    private lateinit var maxDistanceEditText: EditText
    private lateinit var DisplayName: TextView
    private lateinit var DisplayEmail: TextView

    private  lateinit var viewModel: GlobalPreferences
    private  lateinit var userModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()

        viewModel = ViewModelProvider(requireActivity()).get(GlobalPreferences::class.java)
        userModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "")
        val userViewModel: UserViewModel by activityViewModels()
        if (userName != null) {
            userViewModel.userEmail = userName
            DisplayName = view.findViewById(R.id.txtFName)
            DisplayName.setText(userName)
        }



        val userEmail = sharedPreferences.getString("userEmail", "")
        if (userEmail != null) {
            userViewModel.userEmail = userEmail
            DisplayEmail = view.findViewById(R.id.txtEmail)
            DisplayEmail.setText(userEmail)
        }





        measuringSystemSpinner = view.findViewById(R.id.spnMeasuringSystem) as Spinner
        //Setting the global preference for max distance
        maxDistanceEditText = view.findViewById(R.id.txtMaximumDistance) as EditText


        FillSpinner()


        //SETS THE GLOBAL PREFERENCE THE USERS CHOICE FROM THE SPINNER
        measuringSystemSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMeasurementSystem = measuringSystemSpinner.selectedItem as String
                if (selectedMeasurementSystem != viewModel.SelectedMeasuringSystem) {
                    // Set the selected measurement system in the ViewModel
                    viewModel.SelectedMeasuringSystem = selectedMeasurementSystem
                    updateMaxDistance()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        logoutButton = view.findViewById(R.id.btnLogout) as Button
        logoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to log out?")
            builder.setPositiveButton("Yes") { _, _ ->
                // User clicks "Yes", logout
                val intent = Intent(requireContext(), SplashScreen::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                // User clicks "No", don't logout
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
        return view
    }


    private fun updateMaxDistance() {
        val selectedMeasurementSystem = measuringSystemSpinner.selectedItem as String
        val maxDistanceText = maxDistanceEditText.text.toString()
        var updatedMaxDistance = 0.0

        if (maxDistanceText.isNotEmpty()) {
            val maxDistance = maxDistanceText.toDouble()
            if (selectedMeasurementSystem == "Kilometers"){
                updatedMaxDistance = maxDistance * 1.621371
            } else if (selectedMeasurementSystem == "Miles"){
                updatedMaxDistance = maxDistance / 1.621371
            }
            // Log the updatedMaxDistance to ensure that the conversion is correct
            Log.d("MaxDistance", "Updated Max Distance: $updatedMaxDistance")
            maxDistanceEditText.setText(String.format("%.2f", updatedMaxDistance))
            viewModel.MaximumDistance = updatedMaxDistance
            Log.d("MaxDistance", "Selected Measurement System: $selectedMeasurementSystem")
            Log.d("MaxDistance", "Max Distance Text: $maxDistanceText")
            Log.d("MaxDistance", "Updated Max Distance: $updatedMaxDistance")
        }
    }

    private fun showMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    //ADDS THE PREFERENCE OPTIONS TO THE SPINNER
    private fun FillSpinner(){
        //ArrayAdapter that accesses the string array from string.xml
        val adapter = ArrayAdapter.createFromResource(requireContext(),
            R.array.measuring_system_preferences,
            android.R.layout.simple_spinner_dropdown_item
        )

        //Fills the spinners adapter with the array
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        measuringSystemSpinner.adapter = adapter

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}