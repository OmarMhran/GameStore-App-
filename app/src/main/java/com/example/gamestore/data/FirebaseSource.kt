package com.example.gamestore.data

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.gamestore.data.model.Product
import com.example.gamestore.data.model.Seller
import com.example.gamestore.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.merge
import javax.inject.Inject


class FirebaseSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val firebaseDatabase: FirebaseDatabase
) {

    fun signUpUser(email: String, password: String, fullName: String) =
            firebaseAuth.createUserWithEmailAndPassword(email, password)


    fun signInUser(email: String, password: String) =
            firebaseAuth.signInWithEmailAndPassword(email, password)


    fun saveUser(user: User,uid : String)=
            firestore.collection("users").document(uid).set(user)

    fun fetchUser(uid: String)=firestore.collection("users").document(uid)

    fun fetchSeller(uid: String)=firestore.collection("sellers").document(uid)

    fun saveSeller(seller: Seller, uid: String) =
        firestore.collection("sellers").document(uid).set(seller)


    fun uploadImage() = firebaseStorage.reference

    fun uploadProductImage() = firebaseStorage.reference

    fun saveProduct(product: Product,uid: String) =
        firebaseDatabase.getReference("products").child(uid)

    fun getSellerProduct(uid: String) =
        firebaseDatabase.getReference("products").child(uid)


    fun getAllProducts() = firebaseDatabase.getReference("products")

    fun sendMessage(uid: String) = firebaseDatabase.getReference("chats").child(uid)

    fun getMessages(uid: String) = firebaseDatabase.getReference("chats").child(uid)

    fun getSellerIncoming(uid: String) = firebaseDatabase.getReference("chats").child(uid)

    fun getSellerChatMessages(uid: String) = firebaseDatabase.getReference("chats").child(uid)


    fun sellerSendMessages(uid: String) = firebaseDatabase.getReference("chats").child(uid)

//    fun sendForgotPassword(email: String) = firebaseAuth.sendPasswordResetEmail(email)


    fun logoutUser() = firebaseAuth.signOut()

}
