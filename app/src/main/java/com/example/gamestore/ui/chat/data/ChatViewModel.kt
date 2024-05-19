package com.example.gamestore.ui.chat.data

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gamestore.data.NetworkConnection
import com.example.gamestore.data.Resource
import com.example.gamestore.data.model.Chat
import com.example.gamestore.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

class ChatViewModel @ViewModelInject constructor(
    private val repository: ChatRepo,
    private val networkConnection: NetworkConnection,
    private val firebaseAuth: FirebaseAuth,
    private var storageReference: StorageReference
) : ViewModel() {


    var sendMessageLiveData = MutableLiveData<Resource<Chat>>()
    var getMessagesLiveData = MutableLiveData<Resource<List<Chat>>>()

    fun sendMessage(uid: String, chat: Chat): LiveData<Resource<Chat>> {
        if (networkConnection.isConnected()) {
            sendMessageLiveData.postValue(Resource.loading(null))

            repository.sendMessage(uid).child(firebaseAuth.currentUser?.uid!!).push().setValue(chat).addOnSuccessListener {
                sendMessageLiveData.postValue(Resource.success(chat))
            }.addOnFailureListener {
                sendMessageLiveData.postValue(Resource.error(null, it.message.toString()))
            }
        } else {
            sendMessageLiveData.postValue(Resource.error(null, "No Internet Connection"))
        }

        return sendMessageLiveData
    }

    fun getMessages(uid: String): LiveData<Resource<List<Chat>>> {
        if (networkConnection.isConnected()) {
            getMessagesLiveData.postValue(Resource.loading(null))

            val chats = ArrayList<Chat>()
            repository.getMessages(uid).child(firebaseAuth.currentUser?.uid!!)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chats.clear()
                        for (i in snapshot.children) {
                                val chat = i.getValue(Chat::class.java)
                                chats.add(chat!!)
                        }
                        getMessagesLiveData.postValue(Resource.success(chats))

                    }
                    override fun onCancelled(error: DatabaseError) {
                        getMessagesLiveData.postValue(Resource.error(null, error.message))

                    }
                })

        } else {
            getMessagesLiveData.postValue(Resource.error(null, "No Internet Connection"))
        }

        return getMessagesLiveData
    }
}
