package com.example.gamestore.ui.seller.home.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.gamestore.R
import com.example.gamestore.data.AppPreference
import com.example.gamestore.data.Status
import com.example.gamestore.databinding.FragmentSellerIncomingBinding
import com.example.gamestore.ui.home.adapter.IncomingMessagesAdapter
import com.example.gamestore.ui.seller.home.data.SellerHomeViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SellerIncomingFragment : Fragment() {
    private var _binding : FragmentSellerIncomingBinding?= null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SellerHomeViewModel>()
    lateinit var incomingMessagesAdapter: IncomingMessagesAdapter
    @Inject lateinit var appPreference: AppPreference
    var sellerName : String ?= null
    var sellerImage: String ?= null
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerIncomingBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()


        lifecycleScope.launch {
            val name = appPreference.userNameFlow.asLiveData()
            name.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                binding.tvSellerNameIncoming.text = it.toString()
                sellerName = it.toString()
            })

            val image = appPreference.userImageFlow.asLiveData()
            image.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                Glide.with(requireContext()).load(it.toString()).into(binding.ivSellerImageIncoming)
                incomingMessagesAdapter.image = it.toString()
            })
        }


        lifecycleScope.launch {
            viewModel.getSellerMessages(firebaseAuth.currentUser?.uid!!).observe(viewLifecycleOwner,
                Observer {

                    when(it.status){
                        Status.LOADING -> {
                            binding.pbIncoming.visibility = View.VISIBLE
                        }

                        Status.SUCCESS -> {
                            binding.pbIncoming.visibility = View.GONE
                            incomingMessagesAdapter.differ.submitList(it.data!!)
                        }

                        Status.ERROR -> {
                            binding.pbIncoming.visibility = View.VISIBLE
                        }

                    }


                })
        }

        incomingMessagesAdapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putString("userId",it.sender)
            Log.d("userIdNavigtion", "${it.sender}")
            findNavController().navigate(R.id.action_sellerIncomingFragment_to_sellerChatFragment,bundle)
        }
    }

    private fun setUpRecyclerView(){
        incomingMessagesAdapter = IncomingMessagesAdapter()
        binding.rvIncomingMessages.apply {
            adapter = incomingMessagesAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}