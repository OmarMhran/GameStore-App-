package com.example.gamestore.data.model

import java.io.Serializable

data class Product (
    var id : String? = null,
    var productName: String? = null,
    var productPrice: String? = null,
    var yearOfParchase: String? = null,
    var productImage: String? = null,
    var sellerId: String? = null,
    var sellerName: String? = null

): Serializable{

}