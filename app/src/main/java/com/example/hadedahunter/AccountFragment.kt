package com.example.hadedahunter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.example.hadedahunter.startup.SplashScreen

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
@Suppress("UNREACHABLE_CODE")
class AccountFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var logoutButton: Button
    private lateinit var measuringSystemSpinner: Spinner
    private lateinit var maxDistanceEditText: EditText

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