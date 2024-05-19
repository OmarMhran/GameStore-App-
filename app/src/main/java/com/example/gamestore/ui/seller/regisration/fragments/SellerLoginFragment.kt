package com.example.gamestore.ui.seller.regisration.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gamestore.R
import com.example.gamestore.data.AppPreference
import com.example.gamestore.data.Status
import com.example.gamestore.data.model.Seller
import com.example.gamestore.databinding.FragmentSellerLoginBinding
import com.example.gamestore.ui.seller.regisration.data.SellerRegistrationViewModel
import com.example.gamestore.ui.seller.home.SellerHomeActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SellerLoginFragment : Fragment() {
    private var _binding: FragmentSellerLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SellerRegistrationViewModel>()
    private var name =""
    private var email =""
    private var image = ""

    @Inject lateinit var appPreference : AppPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Log.d("current user", "${FirebaseAuth.getInstance().currentUser?.email} ")

        binding.btnLoginToSignUpSeller.setOnClickListener {
            findNavController().navigate(R.id.action_sellerLoginFragment_to_sellerSignUpFragment)
        }


        binding.btnLoginSeller.setOnClickListener {
            val emailText = binding.etEmailLoginSeller.text.toString()
            val passwordText = binding.etPasswordLoginSeller.text.toString()
            lifecycleScope.launch {
                viewModel.signInUser(emailText, passwordText).observe(viewLifecycleOwner, {
                    when (it.status) {
                        Status.LOADING -> {
                            binding.pbLoginSeller.visibility = View.VISIBLE
                        }

                        Status.SUCCESS -> {
                            binding.pbLoginSeller.visibility = View.GONE
                            binding.root.showsnackBarLogin("Login successful")
                            toSellerHome()
                            
                        }

                        Status.ERROR -> {
                            binding.pbLoginSeller.visibility = View.GONE
                            binding.root.showsnackBarLogin(it.message!!)
                        }
                    }
                })

            }

        }

        lifecycleScope.launch {
            viewModel.sellerLoginLiveData.observe(viewLifecycleOwner, Observer {
                it.data?.let {
                    name = it.username
                    email = it.email
                    image = it.image

                }

                Log.d("seller", "$name , $email ,$image ")
                saveToDataStore(name,email,image)
            })
        }
    }

    private fun saveToDataStore(name: String, email: String, image: String) {
        lifecycleScope.launch {
            appPreference.saveUser(name,email,image)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toSellerHome(){
        val intent = Intent(requireActivity(), SellerHomeActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}
fun View.showsnackBarLogin(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}