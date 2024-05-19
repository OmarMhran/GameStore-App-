package com.example.gamestore.ui.signup

import com.example.gamestore.data.FirebaseSource
import com.example.gamestore.data.model.User

import javax.inject.Inject

class SignUpRepo @Inject constructor(private val fireBaseSource: FirebaseSource) {


    fun signUpUser(email: String, password: String,fullName: String) = fireBaseSource.signUpUser(email, password,fullName)


    fun saveUser(user: User, uid: String) =
            fireBaseSource.saveUser(user,uid)

//    fun fetchUser() = fireBaseSource.fetchUser()
//
//    fun sendForgotPassword(email: String)=fireBaseSource.sendForgotPassword(email)

}