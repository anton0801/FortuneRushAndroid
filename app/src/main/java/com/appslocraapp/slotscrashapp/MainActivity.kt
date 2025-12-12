package com.appslocraapp.slotscrashapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.appslocraapp.slotscrashapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val windowInsetsCompat = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsCompat.hide(WindowInsetsCompat.Type.navigationBars())
        windowInsetsCompat.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}