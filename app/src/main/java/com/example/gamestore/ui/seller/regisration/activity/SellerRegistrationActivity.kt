package com.example.gamestore.ui.seller.regisration.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.gamestore.R
import com.example.gamestore.databinding.ActivitySellerRegistrationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellerRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController





    }


}