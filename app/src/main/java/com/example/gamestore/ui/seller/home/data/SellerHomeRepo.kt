package com.example.gamestore.ui.seller.home.data

import com.example.gamestore.data.FirebaseSource
import com.example.gamestore.data.model.Product
import com.example.gamestore.data.model.Seller
import javax.inject.Inject

class SellerHomeRepo @Inject constructor(private val fireBaseSource: FirebaseSource) {

    fun logout() = fireBaseSource.logoutUser()

    fun uploadProductImage() = fireBaseSource.uploadProductImage()

    fun saveProduct(product: Product, uid: String) =
        fireBaseSource.saveProduct(product,uid)

    fun getSellerProducts(uid: String) = fireBaseSource.getSellerProduct(uid)

    fun getSellerIncoming(uid: String) = fireBaseSource.getSellerIncoming(uid)

    fun getSellerChatMessages(uid: String) = fireBaseSource.getSellerChatMessages(uid)

    fun sellerSendMessage(uid: String) = fireBaseSource.sellerSendMessages(uid)


}