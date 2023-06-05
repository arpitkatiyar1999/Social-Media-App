package com.example.connect.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.connect.bean.UserBean
import com.example.connect.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    var currentUser: UserBean? = null
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}