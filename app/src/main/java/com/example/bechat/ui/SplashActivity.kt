package com.example.bechat.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.bechat.R
import com.example.bechat.data.local.SharePrefer

class SplashActivity : AppCompatActivity() {

    lateinit var sharePrefer : SharePrefer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharePrefer = SharePrefer(this)
        Handler().postDelayed({
            if (sharePrefer.getStatus()){
                var intent = Intent(this, MainActivity ::class.java)
                startActivity(intent)
                finish()
            }else{
                var intent = Intent(this, LoginActivity ::class.java)
                startActivity(intent)
                finish()
            }
        },3000)
    }

}