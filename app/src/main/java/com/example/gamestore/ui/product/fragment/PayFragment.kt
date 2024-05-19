package com.example.gamestore.ui.product.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gamestore.databinding.FragmentPayBinding
import com.example.gamestore.ui.product.activity.ProductActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class PayFragment : Fragment() {

    private var _binding:  FragmentPayBinding ? =null
    private val binding get() = _binding!!
    lateinit var dialog: DatePickerDialog
    var visaMonth :String?= null
    var visaYear :String?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentPayBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = datePickerDialog()

        binding.btnMonth.setOnClickListener {
            dialog.show()
            // Hide day Selector
            val day = dialog.findViewById<View>(Resources.getSystem().getIdentifier("android:id/day", null, null))
            if (day != null) {
                day.visibility = View.GONE
            }
        }

        binding.btnYear.setOnClickListener {
            dialog.show()
            // Hide day Selector
            val day = dialog.findViewById<View>(Resources.getSystem().getIdentifier("android:id/day", null, null))
            if (day != null) {
                day.visibility = View.GONE
            }
        }


        binding.btnPayNow.setOnClickListener {
            val cardNumber = binding.etCardNumber.text.toString()
            val cardPin = binding.etPinNumber.text.toString()


            when{
                cardNumber.isNullOrEmpty() ->{
                    view.showsnackBar("Card Number mustn't be empty")
                }

                cardPin.isNullOrEmpty() ->{
                    view.showsnackBar("Pin mustn't be empty")
                }
                visaMonth.isNullOrEmpty() ->{
                    view.showsnackBar("Month mustn't be empty")
                }
                visaYear.isNullOrEmpty() ->{
                    view.showsnackBar("Year mustn't be empty")
                }

                else ->{
                    Toast.makeText(requireContext(), "Successfully Paid", Toast.LENGTH_SHORT).show()
                    toHome()
                }

            }

        }

    }


    @SuppressLint("SetTextI18n")
    fun datePickerDialog(): DatePickerDialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), android.R.style.Theme_Holo_Dialog, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            binding.btnYear.text = year.toString()
            binding.btnMonth.text = month.toString()
            visaMonth = month.toString()
            visaYear = year.toString()
        }, year, month, day)
        // Show Date Picker

        return datePickerDialog


    }


    private fun toHome(){
        val intent = Intent(requireActivity(), ProductActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
private fun View.showsnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}