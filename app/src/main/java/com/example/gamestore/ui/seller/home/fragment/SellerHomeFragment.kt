package com.example.gamestore.ui.seller.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.gamestore.R
import com.example.gamestore.data.AppPreference
import com.example.gamestore.databinding.FragmentSellerHomeBinding
import com.example.gamestore.ui.seller.home.data.SellerHomeViewModel
import com.example.gamestore.ui.seller.regisration.activity.SellerRegistrationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SellerHomeFragment : Fragment() {

    private var _binding: FragmentSellerHomeBinding? =null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SellerHomeViewModel>()
    @Inject lateinit var appPreference : AppPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerHomeBinding.inflate(inflater,container,false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_sellerHomeFragment_to_addProductFragment)
        }

        lifecycleScope.launch {
            val name = appPreference.userNameFlow.asLiveData()
            name.observe(viewLifecycleOwner, Observer {
                binding.tvSellerNameHome.text = it.toString()
            })

            val email = appPreference.userEmailFlow.asLiveData()
            email.observe(viewLifecycleOwner, Observer {

            })

            val image = appPreference.userImageFlow.asLiveData()
            image.observe(viewLifecycleOwner, Observer {
                Glide.with(requireContext()).load(it.toString()).into(binding.ivSellerImageHome)
            })
        }

        binding.btnMyProducts.setOnClickListener {
            findNavController().navigate(R.id.action_sellerHomeFragment_to_sellerProductsFragment)
        }

        binding.btnIncomingMessages.setOnClickListener {
            findNavController().navigate(R.id.action_sellerHomeFragment_to_sellerIncomingFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}