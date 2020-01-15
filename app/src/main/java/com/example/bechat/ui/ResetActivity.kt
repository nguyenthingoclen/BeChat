package com.example.bechat.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bechat.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*
import java.lang.Exception

class ResetActivity :AppCompatActivity(){

    private var auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password )
        resetBtn.setOnClickListener {
            val email = emailResetEdittext.text.toString()
            if (email.isEmpty()){
                Toast.makeText(this,"enter email user ", Toast.LENGTH_SHORT).show()
            }else{
                auth.sendPasswordResetEmail(email).addOnCompleteListener(object : OnCompleteListener<Void>{
                    override fun onComplete(p0: Task<Void>) {
                        if (p0.isSuccessful) {
                            Toast.makeText(this@ResetActivity,"check your email ", Toast.LENGTH_SHORT).show()
                            var intent = Intent(this@ResetActivity,LoginActivity ::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                }) .addOnFailureListener(this, object : OnFailureListener {
                    override fun onFailure(p0: Exception) {
                        Toast.makeText(this@ResetActivity, p0.message.toString(), Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }

        toolbarReset.setOnclickIconBack {
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
            finish()
        }
    }
}