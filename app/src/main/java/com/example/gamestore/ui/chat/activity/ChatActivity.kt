package com.example.gamestore.ui.chat.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamestore.data.Status
import com.example.gamestore.data.model.Chat
import com.example.gamestore.databinding.ActivityChatBinding
import com.example.gamestore.ui.chat.data.ChatAdapter
import com.example.gamestore.ui.chat.data.ChatViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.example.gamestore.data.AppPreference
import com.example.gamestore.data.model.Product
import com.example.gamestore.ui.product.activity.ProductActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    private var binding: ActivityChatBinding? = null
    private val viewModel by viewModels<ChatViewModel>()
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var chatAdapter: ChatAdapter
    @Inject
    lateinit var appPreference : AppPreference
    var username :String? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setUpRecyclerView()

        setSupportActionBar(binding?.tbChat?.tbSecondary)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding?.tbChat?.btnBack?.setOnClickListener {
            toProductActivity()
        }

        val product = intent.getSerializableExtra("productChat") as Product

        val sellerId = product.sellerId!!
        binding?.tvSellerNameChat?.text = product.sellerName

        lifecycleScope.launch {
            val name = appPreference.customerNameFlow.asLiveData()
            name.observe(this@ChatActivity, Observer {
              username = it.toString()
            })




        }

        binding?.btnSubmit?.setOnClickListener {
            val message = binding?.etMessage?.text.toString()

            if (message.isNullOrEmpty()) {
                null
            } else {
                hideKeyboard(this)
                sendMessage(message, sellerId)
                binding?.etMessage?.setText("")
            }

        }


        lifecycleScope.launch {
            viewModel.getMessages(sellerId).observe(this@ChatActivity,{
                when (it.status) {
                    Status.LOADING -> {
                        binding?.pbChat?.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding?.pbChat?.visibility = View.GONE
                        chatAdapter.differ.submitList(it.data!!)
                        binding?.rvChat?.adapter = chatAdapter
                    }
                    Status.ERROR -> {
                        binding?.pbChat?.visibility = View.GONE
                        binding?.root?.showsnackBar(it.message.toString())
                    }

                }
            })


        }

    }

    private fun toProductActivity() {
        val intent = Intent(this,ProductActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun sendMessage(message: String, sellerId: String) {
        lifecycleScope.launch {

            val sender = firebaseAuth.currentUser?.uid!!

            val chat = Chat((System.currentTimeMillis()).toInt(), sender, sellerId, message,username)
            viewModel.sendMessage(sellerId, chat).observe(this@ChatActivity, {

                when (it.status) {
                    Status.LOADING -> {
                        binding?.pbChat?.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding?.pbChat?.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        binding?.pbChat?.visibility = View.GONE
                        binding?.root?.showsnackBar(it.message.toString())
                    }

                }

            })
        }
    }

    private fun setUpRecyclerView() {
        chatAdapter = ChatAdapter()

        binding?.rvChat?.apply {
            adapter = chatAdapter
            layoutManager =
                LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
        }

    }

    fun hideKeyboard(activity: Activity) {
        val inputManager: InputMethodManager = activity
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val currentFocusedView = activity.currentFocus
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}


private fun View.showsnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}