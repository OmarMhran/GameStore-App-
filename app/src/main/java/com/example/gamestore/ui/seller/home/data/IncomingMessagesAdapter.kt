package com.example.gamestore.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestore.R
import com.example.gamestore.data.model.Chat
import com.example.gamestore.data.model.Product
import com.makeramen.roundedimageview.RoundedImageView
import de.hdodenhof.circleimageview.CircleImageView


class IncomingMessagesAdapter : RecyclerView.Adapter<IncomingMessagesAdapter.ViewHolder>() {

    var image :String? = null


    private val differCallback = object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.incoming_messages_layout, parent, false)
        return ViewHolder(view)
    }

    private var onItemClickListener: ((Chat) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val incoming = differ.currentList[position]

        holder.tvCustomerName.text = incoming.senderName
        holder.tvCustomerMessage.text = incoming.message
        Glide.with(holder.itemView.context).load(image).into(holder.ivSellerImage)

        holder.arrow.setOnClickListener {
            onItemClickListener?.let{it(incoming)}
        }


    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCustomerName: TextView = itemView.findViewById(R.id.tvCustomerName)
        val tvCustomerMessage: TextView = itemView.findViewById(R.id.tvCustomerMessage)
        val ivSellerImage: CircleImageView = itemView.findViewById(R.id.ivSellerImageIncomingItem)
        val arrow : ImageButton = itemView.findViewById(R.id.ibArrow)

    }



    fun setOnItemClickListener(listener: (Chat) -> Unit){
        onItemClickListener = listener

    }



}