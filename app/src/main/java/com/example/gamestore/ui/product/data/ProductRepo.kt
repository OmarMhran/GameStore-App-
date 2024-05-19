package com.example.gamestore.ui.product.data

import com.example.gamestore.data.FirebaseSource
import javax.inject.Inject

class ProductRepo @Inject constructor(private val fireBaseSource: FirebaseSource)  {

    fun getAllProducts() = fireBaseSource.getAllProducts()


}