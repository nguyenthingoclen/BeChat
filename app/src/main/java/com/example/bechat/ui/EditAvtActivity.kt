package com.example.bechat.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bechat.R
import com.example.bechat.common.Constans
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

import kotlinx.android.synthetic.main.activity_edit_avt.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.bottom_sheet_image.view.*
import java.io.ByteArrayOutputStream

class EditAvtActivity : AppCompatActivity() {

    private var uriImage : Uri?= null
    private var storageReference : StorageReference?= null
    private var auth : FirebaseAuth?= null
    private var currentUser = FirebaseAuth.getInstance().currentUser
    lateinit var reference : DatabaseReference
    var avatarUri : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_avt)
        chooseAvt()
        storageReference = FirebaseStorage.getInstance().getReference("uploads")
        auth = FirebaseAuth.getInstance()
        saveBtn.setOnClickListener {
            uploadImage()
        }
        avatarImg.setOnClickListener {
            chooseAvt()
        }
        toolbarEditAvatar.setOnclickIconBack {
            finish()
        }
    }
    fun chooseAvt(){
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
                        Constans.CODE_REQUEST_PERMISSTION_WRITE
                )
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            }else {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        Constans.REQUEST_IMAGE_CAPTURE
                )
            }

        }
        bottomSheet.libraryImg.setOnClickListener{
            dialog.dismiss()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                this.getIntent().setType("image/*")
                startActivityForResult(intent, Constans.CODE_PICK_IMAGE)
            }else {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constans.CODE_REQUEST_PERMISSTION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constans.CODE_REQUEST_PERMISSTION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    getIntent().setType("image/*")
                    startActivityForResult(intent, Constans.CODE_PICK_IMAGE)
                }
            }
            Constans.REQUEST_IMAGE_CAPTURE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    dispatchTakePictureIntent()
                }
            }
            Constans.CODE_REQUEST_PERMISSTION_WRITE ->{
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent()
                }else {
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.CAMERA),
                            Constans.REQUEST_IMAGE_CAPTURE
                    )
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var image :Bitmap?= null
        if(requestCode== Constans.CODE_PICK_IMAGE){
            if(resultCode== Activity.RESULT_OK){
                uriImage = data!!.data
                image = MediaStore.Images.Media.getBitmap(this.contentResolver, uriImage)
            }
        }
        if (requestCode == Constans.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            image = data?.extras?.get("data") as Bitmap
            var byte = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, byte)
            uriImage = Uri.parse(MediaStore.Images.Media.insertImage(this.contentResolver, image, "Title", null))
        }

        if (image != null){
            Glide.with(this)
                    .load(image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatarImg)
        }


    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.packageManager)?.also {
                startActivityForResult(takePictureIntent, Constans.REQUEST_IMAGE_CAPTURE)
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
            uploadTask?.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                override fun then(p0: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    if (!p0.isSuccessful){
                        throw p0.exception!!
                    }
                    return fileReference!!.downloadUrl
                }
            })?.addOnCompleteListener(object : OnCompleteListener<Uri> {
                override fun onComplete(p0: Task<Uri>) {
                    if (p0.isSuccessful){
                        var uri = p0.result!!
                        avatarUri = uri.toString()
                    }else {
                        avatarUri = null
                    }
                    editAvatar()

                }

            })
        }
    }

    private fun editAvatar(){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser!!.uid)
        reference.child("avatarURL").setValue(avatarUri).addOnCompleteListener {
            Toast.makeText(this,"update successful",Toast.LENGTH_SHORT).show()

            finish()
        }.addOnFailureListener {
            Toast.makeText(this,"update no successful",Toast.LENGTH_SHORT).show()
        }
    }

}
