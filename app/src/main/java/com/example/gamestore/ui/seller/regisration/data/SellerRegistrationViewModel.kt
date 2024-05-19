package com.example.gamestore.ui.seller.regisration.data

import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamestore.data.NetworkConnection
import com.example.gamestore.data.Resource
import com.example.gamestore.data.model.Seller
import com.example.gamestore.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.regex.Matcher
import java.util.regex.Pattern

class SellerRegistrationViewModel @ViewModelInject constructor(
    private val repository: SellerRegistrationRepo,
    private val networkConnection: NetworkConnection,
    private val firebaseAuth: FirebaseAuth,
    private var storageReference: StorageReference
) : ViewModel() {


    private val sellerLiveData = MutableLiveData<Resource<Seller>>()
     val sellerLoginLiveData = MutableLiveData<Resource<Seller>>()
    private val _saveSellerLiveData = MutableLiveData<Resource<Seller>>()
     var imageSellerLiveData = MutableLiveData<Resource<Uri>>()


    fun signUpUser(email: String, password: String, fullName: String): LiveData<Resource<Seller>> {
        when {
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(fullName) -> {
                sellerLiveData.postValue(Resource.error(null, "Field must not be empty"))
            }
            password.length < 8 -> {
                sellerLiveData.postValue(Resource.error(null, "Password must not be less than 8"))
            }

            !isEmailValid(email) -> {
                sellerLiveData.postValue(Resource.error(null, "Please enter valid email"))
            }

            networkConnection.isConnected() -> {
                sellerLiveData.postValue(Resource.loading(null))

                //check if email is not existed before
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                    if (it.result?.signInMethods?.size == 0) {

                        repository.signUpSeller(email, password, fullName)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    sellerLiveData.postValue(
                                        Resource.success(
                                            Seller(
                                                email = email,
                                                username = fullName
                                            )
                                        )
                                    )
                                } else {
                                    sellerLiveData.postValue(
                                        Resource.error(
                                            null,
                                            it.exception?.message.toString()
                                        )
                                    )
                                }
                            }
                    } else {
                        sellerLiveData.postValue(Resource.error(null, "Email already exist"))
                    }
                }
            }
            else -> {
                sellerLiveData.postValue(Resource.error(null, "No internet connection"))
            }
        }
        return sellerLiveData
    }

    fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun saveSeller(seller: Seller) : LiveData<Resource<Seller>> {
        when{
            networkConnection.isConnected() ->{
                _saveSellerLiveData.postValue( Resource.loading(null))
                repository.saveSeller(seller,firebaseAuth.currentUser?.uid!!).addOnCompleteListener {
                    if (it.isSuccessful) {
                        _saveSellerLiveData.postValue(Resource.success(seller))
                    } else {
                        _saveSellerLiveData.postValue(
                            Resource.error(
                                null,
                                it.exception?.message.toString()
                            )
                        )
                    }
                }
            }

            else ->  _saveSellerLiveData.postValue(Resource.error(null, "No internet connection"))
        }

        return _saveSellerLiveData
    }

    fun uploadProfileImage(image: Uri,filePath: String) : LiveData<Resource<Uri>> {
        if (networkConnection.isConnected()) {
            imageSellerLiveData.postValue(Resource.loading(null))

            if(image != null){

                storageReference =  repository.uploadImage().child("sellers_profile_images/").child(filePath)

               storageReference.putFile(image).addOnCompleteListener {
                   if (it.isComplete){
                       storageReference.downloadUrl.addOnSuccessListener {
                           imageSellerLiveData.postValue(Resource.success(it))
                       }
                   }else{
                       storageReference.downloadUrl.addOnFailureListener {
                           imageSellerLiveData.postValue(Resource.error(null,it.message.toString()))
                       }
                   }

                }
            }else{
                imageSellerLiveData.postValue(Resource.error(null, "No Image Selected"))
            }

        } else {
            imageSellerLiveData.postValue(Resource.error(null, "No internet connection"))
        }

        return imageSellerLiveData
    }





    @RequiresApi(Build.VERSION_CODES.N)
    fun signInUser(email: String, password: String): LiveData<Resource<Seller>> {
        when {
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password) -> {
                sellerLoginLiveData.postValue(Resource.error(null, "Enter email and password"))
            }

            !isEmailValid(email) ->{
                sellerLoginLiveData.postValue(Resource.error(null, "Please enter valid email"))
            }

            networkConnection.isConnected() -> {
                sellerLoginLiveData.postValue(Resource.loading(null))

                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                    //check if email exists
                    if (it.result?.signInMethods?.size == 0) {
                        sellerLoginLiveData.postValue(Resource.error(null, "Email does not exist"))
                    } else {
                        repository.signInUser(email, password).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                repository.fetchSeller(firebaseAuth.currentUser?.uid!!).get().addOnSuccessListener { document ->
                                    if (document != null) {
                                        if (document.data?.get("email").toString() == email){
                                            val seller = document.toObject(Seller::class.java)
                                            sellerLoginLiveData.postValue(Resource.success(seller!!))
                                        }else{
                                            sellerLoginLiveData.postValue(Resource.error(null, "Not Registered as Seller"))
                                        }
                                    }else{
                                        sellerLoginLiveData.postValue(Resource.error(null, "Error Occurred While Sign In"))
                                    }


                                }.addOnFailureListener {
                                    sellerLoginLiveData.postValue(Resource.error(null, it.toString()))
                                }

                            } else {
                                sellerLoginLiveData.postValue(Resource.error(null, "Not Registered As A Seller"))

                            }
                        }
                    }
                }
            }
            else -> {
                sellerLoginLiveData.postValue(Resource.error(null, "No internet connection"))
            }
        }
        return sellerLoginLiveData
    }




}