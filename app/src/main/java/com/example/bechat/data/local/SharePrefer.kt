package com.example.bechat.data.local

import android.content.Context
import android.content.SharedPreferences

class SharePrefer (context: Context) {
    private val SHARED_PREFERENCES_NAME = "Be Chat"
    private val STATUS_USER = "status user"
    private val PASSWORD_USER = "password user"
    private val NAME_USER = "name user"
    private var sharedPreferences : SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    fun setStatus( status : Boolean){
        editor.putBoolean(STATUS_USER,status)
        editor.apply()
    }
    fun getStatus(): Boolean{
        return sharedPreferences.getBoolean(STATUS_USER,false)
    }

    fun setPassword( pass : String){
        editor.putString(PASSWORD_USER,pass)
        editor.apply()
    }
    fun getPassword(): String?{
        return sharedPreferences.getString(PASSWORD_USER,null)
    }

    fun setName( name : String){
        editor.putString(NAME_USER,name)
        editor.apply()
    }
    fun getName(): String?{
        return sharedPreferences.getString(NAME_USER,null)
    }

}