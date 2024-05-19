package com.example.gamestore.ui.product.data

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gamestore.data.NetworkConnection
import com.example.gamestore.data.Resource
import com.example.gamestore.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

class ProductViewModel @ViewModelInject constructor(
    private val repository: ProductRepo,
    private val networkConnection: NetworkConnection,
    private val firebaseAuth: FirebaseAuth,
    private var storageReference: StorageReference
): ViewModel(){


    var productsLiveData = MutableLiveData<Resource<List<Product>>>()

    fun getAllProducts() : LiveData<Resource<List<Product>>> {
        if(networkConnection.isConnected()){

            productsLiveData.postValue(Resource.loading(null))
            val listProduct = ArrayList<Product>()

            repository.getAllProducts()
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        listProduct.clear()
                        for (sellerDataSnapshot in snapshot.children) {
                            if (sellerDataSnapshot != null) {
                                for (productDataSnapshot in sellerDataSnapshot.children) {
                                    val product = productDataSnapshot.getValue(Product::class.java)
                                    listProduct.add(product!!)
                                }
                            }
                        }
                        Log.d("AllProduct", "$listProduct")
                        productsLiveData.postValue(Resource.success(listProduct))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        productsLiveData.postValue(Resource.error(null,error.message))

                    }


                })

        }else{
            productsLiveData.postValue(Resource.error(null,"No Internet Connection"))
        }
        return productsLiveData
    }




}