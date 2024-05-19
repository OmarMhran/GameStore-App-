package com.example.gamestore.ui.seller.chat

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamestore.R
import com.example.gamestore.data.AppPreference
import com.example.gamestore.data.Status
import com.example.gamestore.data.model.Chat
import com.example.gamestore.databinding.FragmentSellerChatBinding
import com.example.gamestore.ui.chat.data.ChatAdapter
import com.example.gamestore.ui.seller.home.data.SellerHomeViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SellerChatFragment : Fragment() {
    val args : SellerChatFragmentArgs by navArgs()

    private var _binding: FragmentSellerChatBinding? =null
    private val binding get() = _binding!!
    lateinit var chatAdapter: ChatAdapter
    private val viewModel by viewModels<SellerHomeViewModel>()
    @Inject
    lateinit var appPreference : AppPreference
    @Inject lateinit var firebaseAuth: FirebaseAuth
    var username : String? =null
    var userID: String?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerChatBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        userID = args.userId


        lifecycleScope.launch {
            viewModel.getSellerChatMessages(userID!!).observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        binding.pbChatSeller.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding.pbChatSeller.visibility = View.GONE
                        chatAdapter.differ.submitList(it.data!!)
                        binding.rvChatSeller.adapter = chatAdapter
                    }
                    Status.ERROR -> {
                        binding.pbChatSeller.visibility = View.GONE
                        binding.root.showsnackBar(it.message.toString())
                    }

                }
            })


        }

        binding.btnSubmitSeller.setOnClickListener {
            val message = binding.etMessageSeller.text.toString()

            if (message.isNullOrEmpty()) {
                null
            } else {
                hideKeyboard(requireActivity())
                sendMessage(message, userID!!)
                binding.etMessageSeller.setText("")
            }

        }

    }

    private fun sendMessage(message: String, sellerId: String) {
        lifecycleScope.launch {

            val sender = firebaseAuth.currentUser?.uid!!

            val chat = Chat((System.currentTimeMillis()).toInt(), sender, sellerId, message,username)
            viewModel.sellerSendMessage(userID!!, chat).observe(viewLifecycleOwner, Observer {

                when (it.status) {
                    Status.LOADING -> {
                        binding.pbChatSeller.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding.pbChatSeller.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        binding.pbChatSeller.visibility = View.GONE
                        binding.root.showsnackBar(it.message.toString())
                    }

                }

            })
        }
    }

    private fun setUpRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.rvChatSeller.apply {
            adapter = chatAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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