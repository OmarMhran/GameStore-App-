package com.example.gamestore.ui.seller.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.gamestore.R
import com.example.gamestore.data.AppPreference
import com.example.gamestore.data.Status
import com.example.gamestore.databinding.FragmentSellerProductsBinding
import com.example.gamestore.ui.seller.home.data.SellerHomeViewModel
import com.example.gamestore.ui.seller.home.data.SellerProductsAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SellerProductsFragment : Fragment() {

    private var _binding : FragmentSellerProductsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SellerHomeViewModel>()
    @Inject
    lateinit var appPreference: AppPreference
    private lateinit var sellersProductsAdapter :SellerProductsAdapter
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerProductsBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()



        lifecycleScope.launch {
            val name = appPreference.userNameFlow.asLiveData()
            name.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                binding.tvNameSellerProducts.text = it.toString()
            })

            val image = appPreference.userImageFlow.asLiveData()
            image.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                Glide.with(requireContext()).load(it.toString()).into(binding.ivImageSellerProducts)
            })
        }


        lifecycleScope.launch {
            viewModel.getSellerProducts(firebaseAuth.currentUser?.uid!!).observe(viewLifecycleOwner,{
                when(it.status){
                    Status.SUCCESS -> {
                        binding.pbSellerProducts.visibility = View.GONE
                        sellersProductsAdapter.differ.submitList(it.data)
                    }
                    Status.ERROR -> {
                        binding.pbSellerProducts.visibility = View.GONE
                        binding.root.rootView?.showsnackBar(it.message!!)
                    }
                    Status.LOADING -> {
                        binding.pbSellerProducts.visibility = View.VISIBLE
                    }


                }


            })
        }

    }

    private fun setUpRecyclerView() {
        sellersProductsAdapter = SellerProductsAdapter()
        binding.rvSellerProducts.apply {
            adapter = sellersProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

private fun View.showsnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}