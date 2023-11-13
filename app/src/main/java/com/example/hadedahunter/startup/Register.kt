package com.example.hadedahunter.startup

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    private lateinit var registerButton: Button
    public override fun onStart() {
        super.onStart()
        // Firebase authentication instance
        val auth = Firebase.auth
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // Firebase authentication instance
        val auth = Firebase.auth
        val signIn : TextView = findViewById(R.id.alreadyLogged)
        signIn.setOnClickListener()
        {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
            finish()
        }

        supportActionBar?.hide()

        registerButton = findViewById(R.id.btnRegister)
        registerButton.setOnClickListener()
        {
            val name : EditText = findViewById(R.id.nameEditTxt)
            val email : EditText = findViewById(R.id.emailEditTxt)
            val password : EditText = findViewById(R.id.passwordEditTxt)

            val userName = name.text.toString()
            val userEmail = email.text.toString()
            val userPassword = password.text.toString()

            if(TextUtils.isEmpty(userName))
            {
                Toast.makeText(this, "Enter a name!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(userEmail))
            {
                Toast.makeText(this, "Enter an email!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(userPassword))
            {
                Toast.makeText(this, "Enter a password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Creating a new user account with their email and password
            auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // If sign up is successful, display a message to the user.
                        Toast.makeText(baseContext, "Registration successful :)",
                            Toast.LENGTH_SHORT).show()
                        // Saving the name in shared preferences
                        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("userName", userName)
                        editor.apply()
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(baseContext, "Registration failed :(",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}