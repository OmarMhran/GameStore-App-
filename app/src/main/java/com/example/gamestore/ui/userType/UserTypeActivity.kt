package com.example.gamestore.ui.userType

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gamestore.databinding.ActivityUserTypeBinding
import com.example.gamestore.ui.login.LoginActivity
import com.example.gamestore.ui.seller.regisration.activity.SellerRegistrationActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserTypeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUserTypeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnToLogin.setOnClickListener {
            toLogin()
        }


        binding.btnToSeller.setOnClickListener {
            toSellerActivity()
        }



    }


    fun toLogin(){
        val intent = Intent(this@UserTypeActivity, LoginActivity::class.java)
        startActivity(intent)
    }
    fun toSellerActivity(){
        val intent = Intent(this@UserTypeActivity, SellerRegistrationActivity::class.java)
        startActivity(intent)
    }
}