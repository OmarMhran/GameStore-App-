package com.example.gamestore.ui.login

import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gamestore.data.NetworkConnection
import com.example.gamestore.data.Resource
import com.example.gamestore.data.model.User
import com.example.gamestore.ui.user.login.LoginRepo
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginViewModel @ViewModelInject constructor(
    private val repository: LoginRepo,
    private val networkConnection: NetworkConnection,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val userLiveData = MutableLiveData<Resource<User>>()
    private val sendResetPasswordLiveData = MutableLiveData<Resource<User>>()


    @RequiresApi(Build.VERSION_CODES.N)
    fun signInUser(email: String, password: String): LiveData<Resource<User>> {
        when {
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password) -> {
                userLiveData.postValue(Resource.error(null, "Enter email and password"))
            }

            !isEmailValid(email) ->{
                userLiveData.postValue(Resource.error(null, "Please enter valid email"))
            }

            networkConnection.isConnected() -> {
                userLiveData.postValue(Resource.loading(null))

                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                    //check if email exists
                    if (it.result?.signInMethods?.size == 0) {
                        userLiveData.postValue(Resource.error(null, "Email does not exist"))
                    } else {

                        repository.signInUser(email, password).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                repository.fetchUser(firebaseAuth.currentUser?.uid!!).get().addOnSuccessListener { document ->
                                    if (document != null){
                                        if (document.data?.get("email").toString() == email){
                                            val user = document.toObject(User::class.java)
                                            userLiveData.postValue(Resource.success(user!!))
                                        }else{
                                            userLiveData.postValue(Resource.error(null, "Not Registered as A Customer"))
                                        }

                                    }else{
                                        userLiveData.postValue(Resource.error(null, "Error Occurred While Sign In"))
                                    }


                                }.addOnFailureListener {
                                    userLiveData.postValue(Resource.error(null, it.toString()))
                                }

                            } else {
                                userLiveData.postValue(Resource.error(null, "Not Registered As A Customer"))

                            }
                        }
                    }
                }
            }
            else -> {
                userLiveData.postValue(Resource.error(null, "No internet connection"))
            }
        }
        return userLiveData
    }


    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

//    fun sendResetPassword(email: String): LiveData<Resource<User>> {
//
//        when {
//            TextUtils.isEmpty(email) -> {
//                sendResetPasswordLiveData.postValue(Resource.error(null, "Enter registered email"))
//            }
//            networkConnection.isConnected() -> {
//                repository.sendForgotPassword(email).addOnCompleteListener { task ->
//                    sendResetPasswordLiveData.postValue(Resource.loading(null))
//                    if (task.isSuccessful) {
//                        sendResetPasswordLiveData.postValue(Resource.success(User()))
//                    } else {
//                        sendResetPasswordLiveData.postValue(
//                                Resource.error(
//                                        null,
//                                        task.exception?.message.toString()
//                                )
//                        )
//                    }
//                }
//            }
//            else -> {
//                sendResetPasswordLiveData.postValue(Resource.error(null, "No internet connection"))
//            }
//        }
//        return sendResetPasswordLiveData
//    }
}