package com.example.bechat.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bechat.R
import com.example.bechat.data.local.SharePrefer
import com.example.bechat.helper.Util
import com.example.bechat.helper.Util.hideKeyboard
import kotlinx.android.synthetic.main.activity_login.*
import com.example.bechat.helper.Util.onRightDrawableClicked
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class LoginActivity : AppCompatActivity(),View.OnClickListener{

    private var auth = FirebaseAuth.getInstance()
    private var hidePass = true
    lateinit var sharedPrefer :SharePrefer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPrefer = SharePrefer(this)
        //set onclick
        passwordEdittext.onRightDrawableClicked{
            if(hidePass){
                it.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_no_hide,0)
                it.inputType = InputType.TYPE_CLASS_TEXT
                it.setSelection(it.text.length)
                hidePass = false
            }else{
                it.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_hide,0)
                it.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                it.setSelection(it.text.length)
                hidePass = true
            }
        }
        loginBtn.setOnClickListener(this)
        signUpText.setOnClickListener(this)
        forgottext.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.loginBtn ->{
                hideKeyboard(this,v)
                if (checkValue()){
                    login()
                }
            }
            R.id.signUpText->{
               val intent = Intent(this, SignUpActivity :: class.java)
                startActivity(intent)
                finish()
            }
            R.id.forgottext ->{
                val intent = Intent(this, ResetActivity :: class.java)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun login(){
        auth.signInWithEmailAndPassword(emailEdittext.text.toString(), passwordEdittext.text.toString())
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0.isSuccessful) {
                            sharedPrefer.setStatus(true)
                            var intent = Intent(this@LoginActivity,MainActivity ::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                })
                .addOnFailureListener(this, object : OnFailureListener {
                    override fun onFailure(p0: Exception) {
                        Toast.makeText(this@LoginActivity, p0.message.toString(), Toast.LENGTH_SHORT).show()
                    }

                })
    }

    private fun checkValue(): Boolean{
        var userEmail = emailEdittext.text.toString()
        var pass = passwordEdittext.text.toString()

        if (userEmail.isEmpty()){
            Toast.makeText(this,"enter email user ",Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass.length < 8){
            Toast.makeText(this,"password invalid",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}