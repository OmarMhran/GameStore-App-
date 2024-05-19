package com.example.gamestore.ui.chat.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestore.R
import com.example.gamestore.data.model.Chat
import com.example.gamestore.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.chat_item_customer_layout.view.*
import kotlinx.android.synthetic.main.chat_item_seller_layout.view.*
import javax.inject.Inject

class ChatAdapter() : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    @Inject lateinit var firebaseAuth : FirebaseAuth
    var sellerImage : String? = null



    companion object{
        const val MSG_TYPE_SELLER = 0
        const val MSG_TYPE_CUSTOMER = 1

    }

    private val differCallback = object : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
       if (viewType == MSG_TYPE_CUSTOMER){
           val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_customer_layout,parent,false)
           return OtherMessageViewHolder(view)
       }else{
           val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_seller_layout,parent,false)
           return MyMessageViewHolder(view)
       }
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        val chat = differ.currentList[position]
        holder.bind(chat)
    }

   open class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
       open fun bind(chat: Chat) {}

    }

    inner class MyMessageViewHolder (view: View) : ViewHolder(view) {
        private var messageText: TextView = view.tvSellerMessage

        override fun bind(chat: Chat) {
            messageText.text = chat.message
        }
    }

    inner class OtherMessageViewHolder (view: View) : ViewHolder(view) {
        private var messageText: TextView = view.tvChatMessage

        override fun bind(chat: Chat) {
            messageText.text = chat.message

        }
    }


    override fun getItemViewType(position: Int): Int {
        //if sender == Seller
        firebaseAuth = FirebaseAuth.getInstance()
        if (differ.currentList[position].sender == firebaseAuth.currentUser?.uid){
            return MSG_TYPE_CUSTOMER
        }else{
            return MSG_TYPE_SELLER
        }
    }
}