package com.example.bechat.ui.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bechat.R
import com.example.bechat.common.Constans.CODE_PICK_IMAGE
import com.example.bechat.common.Constans.CODE_REQUEST_PERMISSTION
import com.example.bechat.common.Constans.CODE_REQUEST_PERMISSTION_WRITE
import com.example.bechat.common.Constans.REQUEST_IMAGE_CAPTURE
import com.example.bechat.data.local.SharePrefer
import com.example.bechat.model.User
import com.example.bechat.ui.EditAvtActivity
import com.example.bechat.ui.LoginActivity
import com.example.bechat.ui.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.bottom_sheet_image.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.sheet_select_profile.view.*

class ProfileFragment :Fragment(){

    private var currentUser = FirebaseAuth.getInstance().currentUser
    lateinit var sharePrefer : SharePrefer
    private var user : User? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharePrefer = SharePrefer(context!!)
        var reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                 user = p0.getValue(User ::class.java)!!
                if (avatarUserProfile != null){
                    nameUserProfile.text = user?.username
                    Glide.with(this@ProfileFragment)
                            .load(user?.avatarURL)
                            .apply(RequestOptions.circleCropTransform())
                            .into(avatarUserProfile)
                    emailUserProfile.text = currentUser!!.email
                }

            }

        })

        avatarUserProfile.setOnClickListener {
            val bottomSheet = layoutInflater.inflate(R.layout.sheet_select_profile, null)
            val dialog = BottomSheetDialog(context!!)
            dialog.setContentView(bottomSheet)
            if (user?.avatarURL == "default"){
                bottomSheet.viewAvatarTx.visibility = View.GONE
            }else{
                bottomSheet.viewAvatarTx.visibility = View.VISIBLE
            }
            dialog.show()
            bottomSheet.viewAvatarTx.setOnClickListener{
                view.findNavController().navigate(R.id.ViewProfileFragment)
                dialog.dismiss()
            }
            bottomSheet.editAvatarTx.setOnClickListener {
                var intent = Intent(activity, EditAvtActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
               // editAvatar()
            }
            bottomSheet.cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }


        editUserNameTx.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("type","name")
            view.findNavController().navigate(R.id.EditProfileFragment,bundle)
        }

        editPasswordTx.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("type","password")
            view.findNavController().navigate(R.id.EditProfileFragment,bundle)
        }



        logoutBtn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            sharePrefer.setStatus(false)
            sharePrefer.setPassword("")
            var intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            (activity as MainActivity).finish()
        }
    }

    fun editAvatar(){
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_image, null)
        val dialog = BottomSheetDialog(context!!)
        dialog.setContentView(bottomSheet)
        dialog.show()
        bottomSheet.cameraImg.setOnClickListener{
            dialog.dismiss()
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity as MainActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        CODE_REQUEST_PERMISSTION_WRITE
                )
            }
            if (ContextCompat.checkSelfPermission((activity as MainActivity), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            }else {
                ActivityCompat.requestPermissions(
                        (activity as MainActivity),
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_IMAGE_CAPTURE
                )
            }

        }
        bottomSheet.libraryImg.setOnClickListener{
            dialog.dismiss()
            if (ContextCompat.checkSelfPermission((activity as MainActivity), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                (activity as MainActivity).getIntent().setType("image/*")
                startActivityForResult(intent, CODE_PICK_IMAGE)
            }else {
                ActivityCompat.requestPermissions(
                        (activity as MainActivity),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        CODE_REQUEST_PERMISSTION
                )
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity((activity as MainActivity).packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

}