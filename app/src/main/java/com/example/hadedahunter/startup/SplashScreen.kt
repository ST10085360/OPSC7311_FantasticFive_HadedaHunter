package com.example.hadedahunter.startup

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.hadedahunter.R

class SplashScreen : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 5000
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()
        mediaPlayer = MediaPlayer.create(this, R.raw.hadeda_call)
        Handler().postDelayed({

            val intent = Intent(this, Login::class.java)
            startActivity(intent)

            mediaPlayer.release()

            finish()

        }, SPLASH_DELAY)
        mediaPlayer.start()
    }
}