package com.example.gamestore.ui.seller.regisration.data

import com.example.gamestore.data.FirebaseSource
import com.example.gamestore.data.model.Seller
import javax.inject.Inject

class SellerRegistrationRepo @Inject constructor(private val fireBaseSource: FirebaseSource) {


    fun signInUser(email: String, password: String) = fireBaseSource.signInUser(email, password)

    fun fetchSeller(uid: String) = fireBaseSource.fetchSeller(uid)

    fun signUpSeller(email: String, password: String,fullName: String) = fireBaseSource.signUpUser(email, password,fullName)


    fun saveSeller(seller: Seller, uid: String) =
        fireBaseSource.saveSeller(seller,uid)


    fun uploadImage() = fireBaseSource.uploadImage()



//    fun fetchUser() = fireBaseSource.fetchUser()
//
//    fun sendForgotPassword(email: String)=fireBaseSource.sendForgotPassword(email)

}