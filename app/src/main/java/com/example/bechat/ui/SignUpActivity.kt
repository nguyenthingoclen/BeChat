package com.example.bechat.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.usage.StorageStats
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.text.InputType
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bechat.R
import com.example.bechat.data.local.SharePrefer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import com.example.bechat.helper.Util.onRightDrawableClicked
import com.google.android.gms.tasks.Continuation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.*
import kotlinx.android.synthetic.main.bottom_sheet_image.view.*

class SignUpActivity : AppCompatActivity(){

    private val CODE_REQUEST_PERMISSTION_WRITE = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private val CODE_REQUEST_PERMISSTION = 3
    private val CODE_PICK_IMAGE = 4
    private var auth : FirebaseAuth?= null
    private var hidePass1 = true
    private var hidePass2 = true
    private var uriImage : Uri?= null
    private var databaseReference : DatabaseReference?= null
    private var firebaseUser : FirebaseUser?= null
    private var storageReference : StorageReference?= null
    private var avatarUri :String?= null
    lateinit var sharedPreferences : SharePrefer
    lateinit var reference : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser
        storageReference = FirebaseStorage.getInstance().getReference("uploads")
        sharedPreferences = SharePrefer(context = this)

        passWordRegisterEdit.onRightDrawableClicked{
            if(hidePass1){
                it.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_no_hide,0)
                it.inputType = InputType.TYPE_CLASS_TEXT
                it.setSelection(it.text.length)
                hidePass1 = false
            }else{
                it.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_hide,0)
                it.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                it.setSelection(it.text.length)
                hidePass1 = true
            }
        }

        password2RegisterEdit.onRightDrawableClicked{
            if(hidePass2){
                it.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_no_hide,0)
                it.inputType = InputType.TYPE_CLASS_TEXT
                it.setSelection(it.text.length)
                hidePass2 = false
            }else{
                it.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_hide,0)
                it.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                it.setSelection(it.text.length)
                hidePass2 = true
            }
        }

        avatarRegisterImg.setImageResource(R.drawable.ic_user)
        avatarRegisterImg.setOnClickListener {
            val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_image, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bottomSheet)
            dialog.show()
            bottomSheet.cameraImg.setOnClickListener{
                dialog.dismiss()
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            CODE_REQUEST_PERMISSTION_WRITE
                    )
                }
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                }else {
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_IMAGE_CAPTURE
                    )
                }

            }
            bottomSheet.libraryImg.setOnClickListener{
                dialog.dismiss()
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    getIntent().setType("image/*")
                    startActivityForResult(intent, CODE_PICK_IMAGE)
                }else {
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            CODE_REQUEST_PERMISSTION
                    )
                }
            }


        }

        loginBtnRegis.setOnClickListener {
            if (checkValue()){
                if(uriImage == null){
                    register()
                }else{
                    uploadImage()
                }


            }
        }
        toolbarSignUp.setOnclickIconBack {
            val intent = Intent(this, LoginActivity :: class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun register(){
            auth?.createUserWithEmailAndPassword(emailRegisterEdit.text.toString(), passWordRegisterEdit.text.toString())
                    ?.addOnCompleteListener(this) { p0 ->
                        if (p0.isSuccessful) {


                            var firebaseUser = auth!!.currentUser
                            var userId = firebaseUser?.uid
                            var friends = mutableListOf<String>()
                            friends.add(userId!!)

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
                            val user = HashMap<String,Any>()
                            user.put("id",userId)
                            user.put("username",userNameRegisterEdit.text.toString())
                            user.put("friends",friends)
                            Log.d("TAG:SignUpActivity","avatarURL "+avatarUri)
                            if(avatarUri == null){
                                user.put("avatarURL","default")
                            }else{
                                user.put("avatarURL", avatarUri!!)
                            }


                            reference.setValue(user).addOnCompleteListener(object : OnCompleteListener<Void> {
                                override fun onComplete(p0: Task<Void>) {
                                    sharedPreferences.setStatus(true)
                                    var intent = Intent(this@SignUpActivity,MainActivity ::class.java)
                                    startActivity(intent)
                                    finish()
                                }

                            })

                        }
                    }
                    ?.addOnFailureListener(this) { p0 ->
                        Toast.makeText(this, p0.message.toString(), Toast.LENGTH_SHORT).show() }


    }

    private fun checkValue(): Boolean{
        var userName = userNameRegisterEdit.text.toString()
        var userEmail = emailRegisterEdit.text.toString()
        var pass1 = passWordRegisterEdit.text.toString()
        var pass2 = password2RegisterEdit.text.toString()

        if(userName.isEmpty()){
            Toast.makeText(this,"enter name user ",Toast.LENGTH_SHORT).show()
            return false
        }
        if (userEmail.isEmpty()){
            Toast.makeText(this,"enter email user ",Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass1.length < 8){
            Toast.makeText(this,"password invalid",Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass1 != pass2){
            Toast.makeText(this,"password no match",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CODE_REQUEST_PERMISSTION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    getIntent().setType("image/*")
                    startActivityForResult(intent, CODE_PICK_IMAGE)
                }
            }
            REQUEST_IMAGE_CAPTURE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    dispatchTakePictureIntent()
                }
            }
            CODE_REQUEST_PERMISSTION_WRITE->{
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                }else {
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_IMAGE_CAPTURE
                    )
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==CODE_PICK_IMAGE){
            if(resultCode== Activity.RESULT_OK){
                avatarRegisterImg.setImageDrawable(null)
                uriImage = data!!.data
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            avatarRegisterImg.setImageDrawable(null)
            var image = data?.extras?.get("data") as Bitmap
            uriImage = Uri.parse(MediaStore.Images.Media.insertImage(this.contentResolver, image, "Title", null))
        }
        Glide.with(this)
                .load(uriImage)
                .apply(RequestOptions.circleCropTransform())
                .into(avatarRegisterImg)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun getFileExtension(uri: Uri): String?{
        var mimeTypeMap =  MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage(){
        if(uriImage != null){
            var fileReference = storageReference?.child(System.currentTimeMillis().toString() + "."+getFileExtension(uriImage!!))
            val uploadTask = fileReference?.putFile(uriImage!!)
            uploadTask?.continueWithTask(object : Continuation<UploadTask.TaskSnapshot,Task<Uri>>{
                override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    Log.d("TAG:SignUpActivity","continueWith "+ p0.isSuccessful.toString())
                    if (!p0.isSuccessful){
                        throw p0.exception!!
                    }
                    return fileReference!!.downloadUrl
                }
            })?.addOnCompleteListener(object : OnCompleteListener<Uri>{
                override fun onComplete(p0: Task<Uri>) {
                    Log.d("TAG:SignUpActivity","addOnCompleteListener "+p0.result!!.toString())
                    if (p0.isSuccessful){
                        var uri = p0.result!!
                        avatarUri = uri.toString()
                    }else {
                        avatarUri = null
                    }
                    register()

                }

            })
        }
    }

}

