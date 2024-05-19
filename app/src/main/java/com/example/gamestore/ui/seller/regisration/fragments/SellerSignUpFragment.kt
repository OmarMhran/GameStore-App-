package com.example.gamestore.ui.seller.regisration.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.gamestore.R
import com.example.gamestore.data.Status
import com.example.gamestore.data.model.Seller
import com.example.gamestore.databinding.FragmentSellerSignUpBinding
import com.example.gamestore.ui.seller.regisration.data.SellerRegistrationViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException


@AndroidEntryPoint
class SellerSignUpFragment : Fragment() {

    private var _binding: FragmentSellerSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SellerRegistrationViewModel>()
    var bm: Bitmap? = null
    var imageUri: Uri? = null
    lateinit var seller: Seller
    val REQUEST_CODE = 1000


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerSignUpBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnSignUpSeller.setOnClickListener {
            val name = binding.etNameSignUpSeller.text.toString()
            val email = binding.etEmailSignUpSeller.text.toString()
            val phone = binding.etPhoneSignUpSeller.text.toString()
            val password = binding.etPasswordSignUpSeller.text.toString()

            when {
                imageUri == null -> {
                    view.showsnackBar("Please Select Your Profile Image")
                }
                phone.length != 8 -> {
                    view.showsnackBar("Phone Number must be 8 digit")
                }
                password.length < 8 -> {
                    view.showsnackBar("Password mustn't be less than 8 digit")
                }


                else -> {
                    signUpSeller(email, password, name)

                }
            }


        }



        binding.ivSellerImage.setOnClickListener {
            openGalleryForImage()
        }

        binding.tvHaveAccountSeller.setOnClickListener {
            toSignIn()
        }

    }

    private fun signUpSeller(email: String, password: String, name: String) {


        lifecycleScope.launch {
            viewModel.signUpUser(email, password, name).observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.pbSignUpSeller.visibility = View.GONE
                        uploadImage()

                    }
                    Status.ERROR -> {
                        binding.pbSignUpSeller.visibility = View.GONE
                        binding.root.rootView?.showsnackBar(it.message!!)
                    }
                    Status.LOADING -> {
                        binding.pbSignUpSeller.visibility = View.VISIBLE
                    }
                }
            })
        }

    }

    fun uploadImage() {
        if (imageUri != null) {
            lifecycleScope.launch {
                viewModel.uploadProfileImage(
                    imageUri!!,
                    (FirebaseAuth.getInstance().currentUser?.uid + getFileExtension(imageUri).toString())
                ).observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        Status.SUCCESS -> {
                            binding.pbSignUpSeller.visibility = View.GONE
                            saveSeller()
                        }


                        Status.ERROR -> {
                            binding.pbSignUpSeller.visibility = View.GONE
                            binding.root.rootView?.showsnackBar(it.message.toString())

                        }
                        Status.LOADING -> {
                            binding.pbSignUpSeller.visibility = View.VISIBLE
                        }
                    }

            })
        }
    }
}

fun saveSeller() {
    lifecycleScope.launch {
        viewModel.imageSellerLiveData.observe(viewLifecycleOwner, Observer {
            val name = binding.etNameSignUpSeller.text.toString()
            val email = binding.etEmailSignUpSeller.text.toString()
            val phone = binding.etPhoneSignUpSeller.text.toString()
            val password = binding.etPasswordSignUpSeller.text.toString()
            val image = it.data.toString()
            seller = Seller(name, email, phone, password, image)
            if (!image.isNullOrEmpty()) {
                viewModel.saveSeller(seller).observe(viewLifecycleOwner, Observer {
                    when (it.status) {
                        Status.SUCCESS -> {
                            binding.pbSignUpSeller.visibility = View.GONE
                            binding.root.rootView?.showsnackBar("User account registered")
                            findNavController().navigate(R.id.action_sellerSignUpFragment_to_sellerLoginFragment)
                        }

                        Status.ERROR -> {
                            binding.pbSignUpSeller.visibility = View.GONE
                            binding.root.rootView?.showsnackBar(it.message.toString())
                        }
                        Status.LOADING -> {
                            binding.pbSignUpSeller.visibility = View.VISIBLE

                        }
                    }


                })
            }
        })
    }
}


override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}

private fun openGalleryForImage() {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_GET_CONTENT
    startActivityForResult(intent, REQUEST_CODE)
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK) {
        if (requestCode == REQUEST_CODE) {
            onSelectFromGalleryResult(data);
        }
    } else {
        Toast.makeText(requireContext(), "No Image Picked", Toast.LENGTH_SHORT).show()
    }
}

private fun onSelectFromGalleryResult(data: Intent?) {
    if (data != null) {
        try {
            bm = MediaStore.Images.Media.getBitmap(
                activity?.applicationContext?.contentResolver,
                data.data
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        imageUri = data.data

    }
    Glide.with(requireContext()).load(bm).into(binding.ivSellerImage)
}


private fun toSignIn() {
    findNavController().navigate(R.id.action_sellerSignUpFragment_to_sellerLoginFragment)
}

private fun getFileExtension(uri: Uri?): String? {
    val contentResolver: ContentResolver = requireContext().contentResolver
    val mimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
}
}

private fun View.showsnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}