package com.geekydroid.managedr.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.geekydroid.managedr.R
import com.geekydroid.managedr.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LandingActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var navController:NavController
    private lateinit var navHostFragment:NavHostFragment

    @Inject
    lateinit var fragmentFactory: ManageDrFragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        supportFragmentManager.fragmentFactory = fragmentFactory

    }

}