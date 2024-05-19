package com.example.gamestore.ui.product.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamestore.R
import com.example.gamestore.databinding.ActivityPaymentBinding
import com.example.gamestore.ui.home.adapter.StepsAdapter
import com.example.gamestore.ui.userType.UserTypeActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_payment.*
import javax.inject.Inject

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

    lateinit var stepsAdapter : StepsAdapter
    private var binding: ActivityPaymentBinding? =null
    @Inject lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setUpRecyclerView()

        setSupportActionBar(binding?.tbPayment?.tbSecondary)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.payment_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            when(destination.id){
                R.id.paymentHomeFragment -> {stepsAdapter.setCountItem(0)}

                R.id.paymentDetailsFragment -> { stepsAdapter.setCountItem(1)}

                R.id.payFragment -> {stepsAdapter.setCountItem(2)}

//                else -> {stepsAdapter.selectedItem = 2}
            }
        }


        binding?.tbPayment?.btnBack?.setOnClickListener {
            toProductActivity()
        }

        binding?.tbPayment?.btnLogout?.setOnClickListener {
            firebaseAuth.signOut()
            toUserType()
        }

    }

    private fun toUserType() {
        val intent = Intent(this@PaymentActivity, UserTypeActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun setUpRecyclerView(){
        stepsAdapter  = StepsAdapter()
        binding?.rvSteps?.apply {
            adapter = stepsAdapter
            layoutManager = LinearLayoutManager(this@PaymentActivity,LinearLayoutManager.HORIZONTAL,false)
        }

    }
    private fun toProductActivity(){
        val intent = Intent(this@PaymentActivity, ProductActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}