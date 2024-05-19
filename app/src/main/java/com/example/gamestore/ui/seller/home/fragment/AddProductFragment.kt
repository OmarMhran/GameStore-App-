package com.example.gamestore.ui.seller.home.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gamestore.R
import com.example.gamestore.databinding.FragmentAddProductBinding
import dagger.hilt.android.AndroidEntryPoint
import android.widget.Toast
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.gamestore.data.AppPreference
import com.example.gamestore.data.Status
import com.example.gamestore.data.model.Product
import com.example.gamestore.data.model.Seller
import com.example.gamestore.ui.seller.home.data.SellerHomeViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AddProductFragment : Fragment() {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SellerHomeViewModel>()

    @Inject
    lateinit var appPreference: AppPreference
    var bm: Bitmap? = null
    var imageUri: Uri? = null
    lateinit var seller: Seller
    val REQUEST_CODE = 1000
    var productName: String? = null
    var productPrice: String? = null
    var productYear: String? = null
    var currentUser: String? = null
    var imageString: String? = null
    var sellerName: String? =null

    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = firebaseAuth.currentUser?.uid.toString()

        val dialog = datePickerDialog()

        binding.btnUploadImageSeller.setOnClickListener {
            openGalleryForImage()
        }

        binding.spinnerYearSeller.setOnClickListener {
            dialog.show()

            // Hide Year Selector
            val day = dialog.findViewById<View>(Resources.getSystem().getIdentifier("android:id/month", null, null))
            val month = dialog.findViewById<View>(Resources.getSystem().getIdentifier("android:id/day", null, null))

            if (day != null && month != null) {
                day.visibility = View.GONE
                month.visibility = View.GONE
            }

        }

        binding.btnPublishProduct.setOnClickListener {
            productName = binding.etSellerProductName.text.toString()
            productPrice = binding.etSellerProductPrice.text.toString()

            when {
                productName.isNullOrEmpty() -> {
                    view.showsnackBar("Product Name Mustn't Be Empty")
                }

                productYear.isNullOrEmpty() ->{
                    view.showsnackBar("Year Of Purchase Mustn't Be Empty")

                }

                productPrice.isNullOrEmpty() -> {
                    view.showsnackBar("Product Price Mustn't Be Empty")
                }
                imageUri == null -> {
                    view.showsnackBar("Product Image Mustn't Be Empty")
                }


                else -> {
                    uploadProductImage()

                }
            }

        }



        lifecycleScope.launch {
            val name = appPreference.userNameFlow.asLiveData()
            name.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                binding.tvSellerNameProduct.text = it.toString()
                sellerName = it.toString()
            })

            val image = appPreference.userImageFlow.asLiveData()
            image.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                Glide.with(requireContext()).load(it.toString()).into(binding.ivSellerImageProduct)
            })
        }


    }

    @SuppressLint("SetTextI18n")
    fun datePickerDialog(): DatePickerDialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Date Picker Dialog
        val datePickerDialog = DatePickerDialog(requireContext(), android.R.style.Theme_Holo_Dialog, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
//            date.text = "$dayOfMonth $monthOfYear, $year"
            productYear = year.toString()
            binding.spinnerYearSeller.text = year.toString()
        }, year, month, day)
        // Show Date Picker

        return datePickerDialog


    }

    private fun uploadProductImage() {
        lifecycleScope.launch {
            viewModel.uploadProductImage(
                imageUri!!,
                System.currentTimeMillis().toString() + getFileExtension(imageUri).toString()
            ).observe(viewLifecycleOwner,{
                when(it.status){
                    Status.SUCCESS -> {
                        binding.pbAddProduct.visibility = View.GONE
                        imageString = it.data.toString()
                        publishProduct()

                    }
                    Status.ERROR -> {
                        binding.pbAddProduct.visibility = View.GONE
                        binding.root.rootView?.showsnackBar(it.message!!)
                    }
                    Status.LOADING -> {
                        binding.pbAddProduct.visibility = View.VISIBLE
                    }
                }



            })
        }


    }

    private fun publishProduct() {

        productName = binding.etSellerProductName.text.toString()
        productPrice = binding.etSellerProductPrice.text.toString()

        lifecycleScope.launch {
            val product =
                Product(currentUser, productName, productPrice, productYear, imageString, currentUser,sellerName)
            viewModel.saveProduct(product).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        binding.pbAddProduct.visibility = View.GONE
                        binding.root.rootView?.showsnackBar("Product Uploaded Succssfully")
                        findNavController().navigate(R.id.action_addProductFragment_to_sellerHomeFragment)
                    }
                    Status.ERROR -> {
                        binding.pbAddProduct.visibility = View.GONE
                        binding.root.rootView?.showsnackBar(it.message!!)
                    }
                    Status.LOADING -> {
                        binding.pbAddProduct.visibility = View.VISIBLE
                    }
                }
            })

        }

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
    }

    private fun getFileExtension(uri: Uri?): String? {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

private fun View.showsnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}