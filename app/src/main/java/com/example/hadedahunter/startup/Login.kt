package com.example.hadedahunter.startup

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.hadedahunter.MainActivity
import com.example.hadedahunter.R
import com.example.hadedahunter.ui.HotspotMap.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var loginButton: Button

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
        setContentView(R.layout.activity_login)
        // Firebase authentication instance
        val auth = Firebase.auth
        val signUp : TextView = findViewById(R.id.noAccount)
        signUp.setOnClickListener()
        {
            val intent = Intent(this,Register::class.java)
            startActivity(intent)
            finish()
        }

        supportActionBar?.hide()

        loginButton = findViewById(R.id.btnLogin)

        val userViewModel: UserViewModel by viewModels()

        loginButton.setOnClickListener()
        {
            val email: EditText = findViewById(R.id.emailEditTxt)
            val password: EditText = findViewById(R.id.passwordEditTxt)

            val userEmail = email.text.toString()
            val userPassword = password.text.toString()

            if (TextUtils.isEmpty(userEmail)) {
                Toast.makeText(this, "Enter an email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(userPassword)) {
                Toast.makeText(this, "Enter a password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful)
                    {
                        // If sign in is successful, display a message to the user.
                        Toast.makeText(baseContext, "Login successful.",
                            Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(baseContext, "Login failed.",
                            Toast.LENGTH_SHORT,).show()
                    }
            }
        }
    }
}
