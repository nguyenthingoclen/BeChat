package com.example.bechat.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.bechat.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity :AppCompatActivity() {

    private var isChatDetailFragment = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomMain.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentPlace)
        val backStackEntryCount = navHostFragment?.childFragmentManager?.fragments?.size

    }
    private val mOnNavigationItemSelectedListener = object : BottomNavigationView.OnNavigationItemSelectedListener {

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when(item.itemId){
                R.id.chatMenuTx ->{
                    this@MainActivity.findNavController(R.id.fragmentPlace).navigate(R.id.chatFragment)
                    return true
                }
                R.id.allUserMenuTx ->{
                    this@MainActivity.findNavController(R.id.fragmentPlace).navigate(R.id.userFragment)
                    return true

                }
                R.id.profileMenuTx ->{
                    this@MainActivity.findNavController(R.id.fragmentPlace).navigate(R.id.profileFragment)
                    return true
                }
            }
            return false
        }

    }
    fun hideBottomNavigation(){
        bottomMain.visibility = View.GONE

    }
    fun showBottomNavigation(){
        bottomMain.visibility = View.VISIBLE
    }

    fun handleronBackPressed(isDetail : Boolean){
       isChatDetailFragment = isDetail
        if (isDetail){
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }else{
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
    }

    override fun onBackPressed() {
        if (isChatDetailFragment){
            showBottomNavigation()
            this@MainActivity.findNavController(R.id.fragmentPlace).navigateUp()
        }
        else{
            super.onBackPressed()
        }


    }


}