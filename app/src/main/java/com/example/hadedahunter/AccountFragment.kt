package com.example.hadedahunter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.hadedahunter.startup.SplashScreen
import com.example.hadedahunter.ui.GlobalPreferences

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
@Suppress("UNREACHABLE_CODE")
class AccountFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var logoutButton: Button
    private lateinit var measuringSystemSpinner: Spinner
    private lateinit var maxDistanceEditText: EditText

    private  lateinit var viewModel: GlobalPreferences

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

        viewModel = ViewModelProvider(requireActivity()).get(GlobalPreferences::class.java)



        measuringSystemSpinner = view.findViewById(R.id.spnMeasuringSystem) as Spinner
        FillSpinner()


        //SETS THE GLOBAL PREFERENCE THE USERS CHOICE FROM THE SPINNER
        measuringSystemSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Set the selected measurement system in the ViewModel
                viewModel.SelectedMeasuringSystem = measuringSystemSpinner.selectedItem as String
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        //Setting the global preference for max distance
        maxDistanceEditText = view.findViewById(R.id.txtMaximumDistance) as EditText

        val dist = maxDistanceEditText.text.toString()

        try {
            viewModel.MaximumDistance = dist.toInt()
        }catch (e: NumberFormatException){
            showMessage("Please enter a valid distance...")
        }

        logoutButton = view.findViewById(R.id.btnLogout) as Button
        logoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to log out?")
            builder.setPositiveButton("Yes") { _, _ ->
                // User clicks "Yes", logout

                // Clearing user-related data
                val sharedPreferences = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("userEmail")
                editor.remove("userPassword")
                editor.apply()

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