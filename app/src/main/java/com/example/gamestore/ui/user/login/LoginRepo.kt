package com.example.gamestore.ui.user.login


import com.example.gamestore.data.FirebaseSource
import javax.inject.Inject

class LoginRepo @Inject constructor(private val fireBaseSource: FirebaseSource) {

     fun signInUser(email: String, password: String) = fireBaseSource.signInUser(email, password)

    fun fetchUser(uid :String) = fireBaseSource.fetchUser(uid)


}