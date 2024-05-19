package com.example.gamestore.ui.seller.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.gamestore.R
import com.example.gamestore.databinding.ActivitySellerHomeBinding
import com.example.gamestore.ui.seller.regisration.activity.SellerRegistrationActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SellerHomeActivity : AppCompatActivity() {

    @Inject lateinit var firebaseAuth: FirebaseAuth

    private lateinit var binding: ActivitySellerHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.seller_home_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController



        binding.tbSellerHome.btnBack.setOnClickListener {
            toLogin()
        }
        binding.tbSellerHome.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            toLogin()
        }
    }

    private fun toLogin(){
        val intent = Intent(this,SellerRegistrationActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}