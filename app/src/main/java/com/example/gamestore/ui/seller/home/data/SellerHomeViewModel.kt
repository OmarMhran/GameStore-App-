package com.example.gamestore.ui.seller.home.data

import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestore.data.NetworkConnection
import com.example.gamestore.data.Resource
import com.example.gamestore.data.model.Chat
import com.example.gamestore.data.model.Product
import com.example.gamestore.data.model.Seller
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SellerHomeViewModel @ViewModelInject constructor(
    private val repository: SellerHomeRepo,
    private val networkConnection: NetworkConnection,
    private val firebaseAuth: FirebaseAuth,
    private var storageReference: StorageReference
    ): ViewModel(){


    var imageProductLiveData = MutableLiveData<Resource<Uri>>()
    var saveProductLiveData = MutableLiveData<Resource<Product>>()
    var sellerProductsLiveData = MutableLiveData<Resource<List<Product>>>()

    var sellerIncomingMessages = MutableLiveData<Resource<List<Chat>>>()
    var sellerChatMessages = MutableLiveData<Resource<List<Chat>>>()

    var sellerSendMessage = MutableLiveData<Resource<Chat>>()


    fun saveProduct(product: Product): LiveData<Resource<Product>>{
        if (networkConnection.isConnected()) {
            imageProductLiveData.postValue(Resource.loading(null))


            repository.saveProduct(product,firebaseAuth.currentUser?.uid.toString())
                .push().setValue(product).addOnSuccessListener {
                    saveProductLiveData.postValue(Resource.success(product))

                }.addOnFailureListener {
                    saveProductLiveData.postValue(Resource.error(null, it.message.toString()))
                }

        }else{
            saveProductLiveData.postValue(Resource.error(null, "No internet connection"))
        }
        return saveProductLiveData
    }


    fun uploadProductImage(image: Uri,filePath: String) : LiveData<Resource<Uri>> {
        if (networkConnection.isConnected()) {
            imageProductLiveData.postValue(Resource.loading(null))

            if(image != null){

                storageReference =  repository.uploadProductImage().child("product_images/").child(filePath)

                storageReference.putFile(image).addOnCompleteListener {
                    if(it.isComplete){
                        storageReference.downloadUrl.addOnSuccessListener {
                            imageProductLiveData.postValue(Resource.success(it))
                        }
                    }else{
                        storageReference.downloadUrl.addOnFailureListener {
                            imageProductLiveData.postValue(Resource.error(null,it.message.toString()))
                        }
                    }


                }
            }else{
                imageProductLiveData.postValue(Resource.error(null, "No Image Selected"))
            }

        } else {
            imageProductLiveData.postValue(Resource.error(null, "No internet connection"))
        }

        return imageProductLiveData
    }




    fun getSellerProducts(uid :String) :LiveData<Resource<List<Product>>>{
        if(networkConnection.isConnected()){

            sellerProductsLiveData.postValue(Resource.loading(null))
            val listProduct = ArrayList<Product>()

            repository.getSellerProducts(uid)
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listProduct.clear()
                        for (i in snapshot.children){
                            val product = i.getValue(Product::class.java)
                            listProduct.add(product!!)

                        }
                        Log.d("listProduct", "$listProduct")
                        sellerProductsLiveData.postValue(Resource.success(listProduct))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        sellerProductsLiveData.postValue(Resource.error(null,error.message))

                    }


                })

        }else{
            sellerProductsLiveData.postValue(Resource.error(null,"No Internet Connection"))
        }
        return sellerProductsLiveData
    }


    fun logout() = viewModelScope.launch(Dispatchers.IO){
        repository.logout()
    }




    fun getSellerMessages(uid: String): LiveData<Resource<List<Chat>>>{
        if(networkConnection.isConnected()){

            sellerIncomingMessages.postValue(Resource.loading(null))
            val listIncoming = ArrayList<Chat>()


            repository.getSellerIncoming(firebaseAuth.currentUser?.uid!!)
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listIncoming.clear()
                        for (i in snapshot.children){
                            for (incomingDataSnapshot in i.children.distinctBy { i.key }) {
                                val chat = incomingDataSnapshot.getValue(Chat::class.java)
                                if (chat?.sender != firebaseAuth.currentUser?.uid!!){
                                    listIncoming.add(chat!!)
                                }

                            }

                        }
                        Log.d("listIncoming", "$listIncoming")
                        sellerIncomingMessages.postValue(Resource.success(listIncoming))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        sellerIncomingMessages.postValue(Resource.error(null,error.message))

                    }


                })

        }else{
            sellerIncomingMessages.postValue(Resource.error(null,"No Internet Connection"))
        }
        return sellerIncomingMessages
    }


    fun getSellerChatMessages(uid: String) : LiveData<Resource<List<Chat>>>{
        if (networkConnection.isConnected()){
            sellerChatMessages.postValue(Resource.loading(null))
            val listChats = ArrayList<Chat>()

            repository.getSellerChatMessages(firebaseAuth.currentUser?.uid!!).child(uid)
                .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    listChats.clear()
                    for (i in snapshot.children){
                        val chat = i.getValue(Chat::class.java)
                        listChats.add(chat!!)

                    }
                    Log.d("listChats", "$listChats")
                    sellerChatMessages.postValue(Resource.success(listChats))
                }
                override fun onCancelled(error: DatabaseError) {
                    sellerChatMessages.postValue(Resource.error(null,error.message))

                }

            })

        }else{
            sellerChatMessages.postValue(Resource.error(null,"No Internet Connection"))
        }

        return sellerChatMessages
    }


    fun sellerSendMessage(uid: String, chat: Chat) : LiveData<Resource<Chat>>{
        if (networkConnection.isConnected()){

            sellerSendMessage.postValue(Resource.loading(null))

            repository.sellerSendMessage(firebaseAuth.currentUser?.uid!!).child(uid).push().setValue(chat).addOnSuccessListener {
                sellerSendMessage.postValue(Resource.success(chat))
            }.addOnFailureListener {
                sellerSendMessage.postValue(Resource.error(null, it.message.toString()))
            }

        }else{
            sellerSendMessage.postValue(Resource.error(null,"No Internet Connection"))
        }
        return sellerSendMessage
    }

}