package com.example.gamestore.ui.signup

import android.text.TextUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gamestore.data.NetworkConnection
import com.example.gamestore.data.Resource
import com.example.gamestore.data.model.User
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpViewModel @ViewModelInject constructor(
    private val repository: SignUpRepo,
    private val networkConnection: NetworkConnection,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val userLiveData = MutableLiveData<Resource<User>>()
    private val _saveUserLiveData = MutableLiveData<Resource<User>>()



    fun signUpUser(email: String, password: String, fullName: String): LiveData<Resource<User>> {
        when {
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty( fullName ) -> {
                userLiveData.postValue(Resource.error(null, "Field must not be empty"))
            }
            password.length < 8 -> {
                userLiveData.postValue( Resource.error(null, "Password must not be less than 8"))
            }

            !isEmailValid(email) ->{
                userLiveData.postValue(Resource.error(null, "Please enter valid email"))
            }

            networkConnection.isConnected() -> {
                userLiveData.postValue(Resource.loading(null))

                //check if email is not existed before
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                    if (it.result?.signInMethods?.size == 0) {

                        repository.signUpUser(email, password, fullName).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                userLiveData.postValue( Resource.success( User( email = email, username = fullName )))
                            } else {
                                userLiveData.postValue( Resource.error(null, it.exception?.message.toString()))
                            } }
                    } else {
                        userLiveData.postValue(Resource.error(null, "Email already exist"))
                    } }
            } else -> {
            userLiveData.postValue(Resource.error(null, "No internet connection"))
        } }
        return userLiveData
    }

    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun saveUser(user: User) {

        repository.saveUser(user,firebaseAuth.currentUser?.uid!!).addOnCompleteListener {
            if (it.isSuccessful) {
                _saveUserLiveData.postValue(Resource.success(user))
            }else{
                _saveUserLiveData.postValue(Resource.error(null,it.exception?.message.toString()))
            }
        }
    }
}