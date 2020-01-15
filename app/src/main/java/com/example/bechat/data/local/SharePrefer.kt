package com.example.bechat.data.local

import android.content.Context
import android.content.SharedPreferences

class SharePrefer (context: Context) {
    private val SHARED_PREFERENCES_NAME = "Be Chat"
    private val STATUS_USER = "status user"
    private var sharedPreferences : SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    fun setStatus( status : Boolean){
        editor.putBoolean(STATUS_USER,status)
        editor.apply()
    }
    fun getStatus(): Boolean{
        return sharedPreferences.getBoolean(STATUS_USER,false)
    }

}