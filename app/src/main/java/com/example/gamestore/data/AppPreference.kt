package com.example.gamestore.data

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AppPreference(context: Context){

    private val appPreference = context.createDataStore("data_prefs")

    suspend fun saveUser(name :String, email:String,image:String){
        appPreference.edit {
            it[USER_NAME] = name
            it[USER_EMAIL] = email
            it[USER_IMAGE] = image
        }
    }


    suspend fun saveUser2(name :String, email:String){
        appPreference.edit {
            it[CUSTOMER_NAME] = name
            it[CUSTOMER_EMAIL] = email
        }
    }
    val userNameFlow: Flow<String> = appPreference.data.map {
        it[USER_NAME] ?: " "
    }

    val userEmailFlow: Flow<String> = appPreference.data.map {
        it[USER_EMAIL] ?: " "
    }

    val userImageFlow: Flow<String> = appPreference.data.map {
        it[USER_IMAGE] ?: " "
    }
    val customerNameFlow: Flow<String> = appPreference.data.map {
        it[CUSTOMER_NAME] ?: " "
    }

    val customerEmailFlow: Flow<String> = appPreference.data.map {
        it[CUSTOMER_EMAIL] ?: " "
    }



    companion object{
        val USER_NAME = preferencesKey<String>("USER_NAME")
        val USER_EMAIL = preferencesKey<String>("USER_EMAIL")
        val USER_IMAGE = preferencesKey<String>("USER_IMAGE")

        val CUSTOMER_NAME = preferencesKey<String>("CUSTOMER_NAME")
        val CUSTOMER_EMAIL = preferencesKey<String>("CUSTOMER_EMAIL")


    }
}