package com.example.hadedahunter.startup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.hadedahunter.R

class SplashScreen : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        registerButton = findViewById(R.id.btnRegister)
        registerButton.setOnClickListener{
            val intent =  Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }

        loginButton = findViewById(R.id.btnLogin)
        loginButton.setOnClickListener{
            val intent =  Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        var image : ImageView = findViewById(R.id.hadedaLogo)
        image.alpha = 0f
        image.animate().setDuration(5000).alpha(1f).withEndAction() {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        var image2 : ImageView = findViewById(R.id.birdsFlocking)
        image2.alpha = 0f
        image2.animate().setDuration(5000).alpha(1f).withEndAction() {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}