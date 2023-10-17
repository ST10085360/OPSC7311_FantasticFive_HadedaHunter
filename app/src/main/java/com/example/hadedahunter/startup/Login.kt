package com.example.hadedahunter.startup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hadedahunter.MainActivity
import com.example.hadedahunter.R

class Login : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signUp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.btnLogin)
        email = findViewById(R.id.emailEditTxt)
        password = findViewById(R.id.passwordEditTxt)
        signUp = findViewById(R.id.noAccount)

        loginButton.setOnClickListener {
            val userEmail = email.text.toString()
            val userPassword = password.text.toString()

            if (TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword))
            {
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val registeredEmail = sharedPreferences.getString("userEmail", "")
                val registeredPassword = sharedPreferences.getString("userPassword", "")

                if (userEmail == registeredEmail && userPassword == registeredPassword)
                {
                    Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        signUp.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }
    }
}
