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
import com.example.hadedahunter.R

class Register : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signIn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton = findViewById(R.id.btnRegister)
        registerButton.setOnClickListener(){
            name = findViewById(R.id.nameEditTxt)
            email = findViewById(R.id.emailEditTxt)
            password = findViewById(R.id.passwordEditTxt)

            var userName = name.text.toString()
            var userEmail = email.text.toString()
            var userPassword = password.text.toString()

            if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(userPassword))
            {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("userName", userName)
                editor.putString("userEmail", userEmail)
                editor.putString("userPassword", userPassword)
                editor.apply()

                Toast.makeText(baseContext, "Registration successful.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,Login::class.java)
                startActivity(intent)
                finish()
            }
        }
        signIn = findViewById(R.id.alreadyLogged)
        signIn.setOnClickListener()
        {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}