package com.example.managedr

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.managedr.databinding.ActivityMainBinding

class LandingActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private var player:MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setContentView(binding.root)

    }

}