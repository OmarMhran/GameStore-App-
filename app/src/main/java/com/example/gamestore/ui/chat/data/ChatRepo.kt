package com.example.gamestore.ui.chat.data

import com.example.gamestore.data.FirebaseSource
import javax.inject.Inject

class ChatRepo @Inject constructor(private val fireBaseSource: FirebaseSource)  {


    fun sendMessage(uid: String) = fireBaseSource.sendMessage(uid)

    fun getMessages(uid: String) = fireBaseSource.getMessages(uid)

}