package com.example.gamestore.ui.product.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamestore.data.Status
import com.example.gamestore.data.model.Product
import com.example.gamestore.databinding.ActivityProductBinding
import com.example.gamestore.ui.chat.activity.ChatActivity
import com.example.gamestore.ui.home.adapter.ProductAdapter
import com.example.gamestore.ui.product.data.ProductViewModel
import com.example.gamestore.ui.userType.UserTypeActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductActivity : AppCompatActivity() {

    private var binding: ActivityProductBinding? = null
    lateinit var productAdapter: ProductAdapter
    private val viewModel by viewModels<ProductViewModel>()
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        setUpRecyclerView()


        lifecycleScope.launch {
            viewModel.getAllProducts().observe(this@ProductActivity, {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding?.pbProduct?.visibility = View.GONE

                        productAdapter.differ.submitList(it.data)
                    }
                    Status.ERROR -> {
                        binding?.pbProduct?.visibility = View.GONE
                        binding?.root?.rootView?.showsnackBar(it.message!!)
                    }
                    Status.LOADING -> {
                        binding?.pbProduct?.visibility = View.VISIBLE
                    }


                }

            })
        }


        productAdapter.setOnItemClickListener {
            toPayment(it)
        }

        productAdapter.setOnItemClickListener2 {
            toChat(it)
        }

        binding?.btnHomeLogout?.setOnClickListener {
            firebaseAuth.signOut()
            toUserType()

        }

    }

    private fun toChat(product: Product) {
        val intent = Intent(this@ProductActivity, ChatActivity::class.java)
        intent.putExtra("productChat", product)
        startActivity(intent)
        this.finish()
    }

    private fun setUpRecyclerView() {
        productAdapter = ProductAdapter()
        binding?.rvProductHome?.apply {
            adapter = productAdapter
            layoutManager =
                LinearLayoutManager(this@ProductActivity, LinearLayoutManager.VERTICAL, false)
        }


    }

    private fun toPayment(product: Product) {
        val intent = Intent(this@ProductActivity, PaymentActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
        this.finish()

    }

    private fun toUserType() {
        val intent = Intent(this@ProductActivity, UserTypeActivity::class.java)
        startActivity(intent)
        this.finish()
    }



    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
private fun View.showsnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}