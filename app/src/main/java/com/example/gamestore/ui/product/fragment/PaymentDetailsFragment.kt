package com.example.gamestore.ui.product.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.gamestore.R
import com.example.gamestore.databinding.FragmentPaymentDetailsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentDetailsFragment : Fragment() {

    private var _binding: FragmentPaymentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentDetailsBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnContinuePayment.setOnClickListener {
            val city = binding.etCity.text.toString()
            val building_num = binding.etBuildingNumber.text.toString()
            val floor_num = binding.etFloorNum.text.toString()

            when{
                city.isNullOrEmpty() ->{
                    view.showsnackBar("City mustn't be empty")
                }

                building_num.isNullOrEmpty() ->{
                    view.showsnackBar("Building Number mustn't be empty")
                }
                floor_num.isNullOrEmpty() ->{
                    view.showsnackBar("Floor Number mustn't be empty")
                }

                else -> findNavController().navigate(R.id.action_paymentDetailsFragment_to_payFragment)

            }


        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
private fun View.showsnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}