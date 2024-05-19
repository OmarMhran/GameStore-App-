package com.example.gamestore.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestore.R
import com.example.gamestore.databinding.StepsItemLayoutBinding

class StepsAdapter : RecyclerView.Adapter<StepsAdapter.StepsViewHolder>() {

    var selectedItem: Int = 0
    val payment = arrayOf("Product", "Personal data", "Pay")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepsViewHolder {
        return StepsViewHolder(StepsItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun getItemCount(): Int {
        return payment.size
    }


    override fun onBindViewHolder(holder: StepsViewHolder, position: Int) {
        val currValue = payment[position]
        holder.bind(currValue)
    }


    inner class StepsViewHolder(private val binding: StepsItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        private val green = binding.root.context.resources.getColor(R.color.color1)
        private val gray = binding.root.context.resources.getColor(R.color.gray)


        fun bind(payment: String) {
            binding.tvStepName.text = payment.toString()




            if(adapterPosition == 0){
                binding.viewLine1.visibility = View.GONE
                binding.cvStep.setBackgroundResource(R.drawable.border_step_background)
                binding.tvStepName.setTextColor(green)
            }else{
                binding.cvStep.setBackgroundResource(R.drawable.gray_step_background)
                binding.tvStepName.setTextColor(gray)
            }


            if (adapterPosition == 2) {
                binding.viewLine2.visibility = View.GONE
                binding.cvStep.visibility = View.VISIBLE
                binding.cvStep.setBackgroundResource(R.drawable.border_step_background)
            }else{
                binding.viewLine2.visibility = View.VISIBLE
            }

            if (adapterPosition <= selectedItem ) {
                binding.cvStep.setBackgroundResource(R.drawable.completed_step)
                binding.tvStepName.setTextColor(green)
            }else{
                binding.cvStep.setBackgroundResource(R.drawable.gray_step_background)
                binding.tvStepName.setTextColor(gray)
            }




        }

    }

    fun ifButtonNextClicked() {
        selectedItem += 1
        this.notifyDataSetChanged()
    }

    fun setCountItem(count: Int){
        this.selectedItem = count
        this.notifyDataSetChanged()
    }


}