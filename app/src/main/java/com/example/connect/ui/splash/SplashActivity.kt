package com.example.connect.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.connect.ui.main.MainActivity
import com.example.connect.databinding.ActivitySplashBinding
import com.example.connect.ui.authentication.AuthenticationActivity
import com.example.connect.utils.AppSharedPref
import com.example.connect.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var fireBaseAuth: FirebaseAuth

    @Inject
    lateinit var sharedPref: AppSharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar()
        handleAppFlow()
    }

    private fun hideStatusBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        supportActionBar?.hide()
    }

    private fun handleAppFlow() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (fireBaseAuth.currentUser != null && sharedPref.isUserInfoAdded) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                }
                finish()
            }, Constants.SPLASH_TIME
        )
    }
}