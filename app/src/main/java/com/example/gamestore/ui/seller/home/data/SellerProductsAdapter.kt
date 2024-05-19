package com.example.gamestore.ui.seller.home.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestore.R
import com.example.gamestore.data.model.Product
import com.makeramen.roundedimageview.RoundedImageView


class SellerProductsAdapter : RecyclerView.Adapter<SellerProductsAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_payment_layout, parent, false)
        return ViewHolder(view)
    }

    private var onItemClickListener: ((Product) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val products = differ.currentList[position]

        holder.tvProductName.text = products.productName
        holder.tvProductPrice.setText("${holder.itemView.context.resources.getString(R.string.price)} ${products.productPrice} ")
        holder.tvProductYear.text = "${holder.itemView.context.resources.getString(R.string.year_of_parchase)} ${products.yearOfParchase} "

        Glide.with(holder.itemView.context).load(products.productImage).into(holder.ivProductImage)

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductNamePayment)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPricePayment)
        val tvProductYear: TextView = itemView.findViewById(R.id.tvProductYearPayment)
        val ivProductImage: RoundedImageView = itemView.findViewById(R.id.ivProductImagePayment)


    }



    fun setOnItemClickListener(listener: (Product) -> Unit){
        onItemClickListener = listener

    }



}