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
import com.example.hadedahunter.startup.Login
import com.example.hadedahunter.ui.GlobalPreferences
import com.example.hadedahunter.ui.HotspotMap.UserViewModel
import com.example.hadedahunter.ui.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AccountFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var logoutButton: Button
    private lateinit var measuringSystemSpinner: Spinner
    private lateinit var maxDistanceEditText: EditText
    private lateinit var displayName: TextView
    private lateinit var displayEmail: TextView

    private lateinit var viewModel: GlobalPreferences
    private lateinit var userModel: UserViewModel

    private lateinit var auth: FirebaseAuth

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

        auth = Firebase.auth

        (activity as AppCompatActivity).supportActionBar?.hide()

        viewModel = ViewModelProvider(requireActivity())[GlobalPreferences::class.java]
        userModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        val sharedPreferences =
            requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "")
        val userViewModel: UserViewModel by activityViewModels()
        if (userName != null) {
            userViewModel.userEmail = userName
            displayName = view.findViewById(R.id.txtFName)
            displayName.text = userName
        }

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email
            if (userEmail != null) {
                userViewModel.userEmail = userEmail
                displayEmail = view.findViewById(R.id.txtEmail)
                displayEmail.text = userEmail
            }
        }


        measuringSystemSpinner = view.findViewById(R.id.spnMeasuringSystem)
        maxDistanceEditText = view.findViewById(R.id.txtMaximumDistance)

        fillSpinner()

        measuringSystemSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMeasurementSystem = measuringSystemSpinner.selectedItem as String
                if (selectedMeasurementSystem != viewModel.SelectedMeasuringSystem) {
                    viewModel.SelectedMeasuringSystem = selectedMeasurementSystem
                    updateMaxDistance()
                    updateFirebasePreferences()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Handle case when nothing is selected if needed
            }
        }

        val userEmail = userViewModel.userEmail

        viewModel.fetchUserPreferences(userEmail) { preferences ->
            if (preferences != null) {
                viewModel.SelectedMeasuringSystem = preferences.measuringSystem
                viewModel.MaximumDistance = preferences.maximumDistance

                // Update UI elements or perform additional actions based on fetched preferences
                // For example, update the spinner selection and max distance edit text
                measuringSystemSpinner.setSelection(
                    (measuringSystemSpinner.adapter as ArrayAdapter<String>).getPosition(viewModel.SelectedMeasuringSystem)
                )
                maxDistanceEditText.setText(viewModel.MaximumDistance.toString())
            }
        }

        logoutButton = view.findViewById(R.id.btnLogout)
        logoutButton.setOnClickListener {
            logout()
        }
        return view
    }

    private fun updateFirebasePreferences() {
        val userViewModel: UserViewModel by activityViewModels()
        val userEmail = Firebase.auth.currentUser?.email.toString()
        val preferences = UserPreferences(
            measuringSystem = viewModel.SelectedMeasuringSystem,
            maximumDistance = viewModel.MaximumDistance
        )
        viewModel.updateUserPreferences(userEmail, preferences)
    }

    private fun updateMaxDistance() {
        val selectedMeasurementSystem = measuringSystemSpinner.selectedItem as String
        val maxDistanceText = maxDistanceEditText.text.toString()
        var updatedMaxDistance = 0.0

        if (maxDistanceText.isNotEmpty()) {
            val maxDistance = maxDistanceText.toDouble()
            if (selectedMeasurementSystem == "Kilometers") {
                updatedMaxDistance = maxDistance * 1.621371
            } else if (selectedMeasurementSystem == "Miles") {
                updatedMaxDistance = maxDistance / 1.621371
            }
            Log.d("MaxDistance", "Updated Max Distance: $updatedMaxDistance")
            maxDistanceEditText.setText(String.format("%.2f", updatedMaxDistance))
            viewModel.MaximumDistance = updatedMaxDistance
            Log.d("MaxDistance", "Selected Measurement System: $selectedMeasurementSystem")
            Log.d("MaxDistance", "Max Distance Text: $maxDistanceText")
            Log.d("MaxDistance", "Updated Max Distance: $updatedMaxDistance")
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun fillSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.measuring_system_preferences,
            android.R.layout.simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        measuringSystemSpinner.adapter = adapter
    }

    private fun logout() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { _, _ ->
            auth.signOut()
            Toast.makeText(requireContext(), "Logout Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
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
