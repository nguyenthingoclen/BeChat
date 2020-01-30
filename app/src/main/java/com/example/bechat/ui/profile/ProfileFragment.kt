package com.example.bechat.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bechat.R
import com.example.bechat.data.local.SharePrefer
import com.example.bechat.model.User
import com.example.bechat.ui.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        }
    }


}