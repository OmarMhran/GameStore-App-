package com.example.gamestore.ui.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.gamestore.R
import com.example.gamestore.data.model.Product
import com.example.gamestore.databinding.FragmentPaymentHomeBinding
import com.example.gamestore.ui.product.activity.PaymentActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentHomeFragment : Fragment() {
    private var _binding: FragmentPaymentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentHomeBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = (activity as PaymentActivity).intent.getSerializableExtra("product") as Product

        binding.productItem.tvProductYearPayment.text = requireContext().resources.getString(R.string.year_of_parchase)+" ${product.yearOfParchase}"
        binding.productItem.tvProductPricePayment.text = requireContext().resources.getString(R.string.price)+" ${product.productPrice}"
        binding.productItem.tvProductNamePayment.text = product.productName
        Glide.with(requireContext()).load(product.productImage).into(binding.productItem.ivProductImagePayment)


        binding.btnContinueHome.setOnClickListener{
            findNavController().navigate(R.id.action_paymentHomeFragment_to_paymentDetailsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}