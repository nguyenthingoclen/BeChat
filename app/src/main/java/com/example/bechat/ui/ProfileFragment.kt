package com.example.bechat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bechat.R
import com.example.bechat.data.local.SharePrefer
import com.example.bechat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment :Fragment(){

    private var currentUser = FirebaseAuth.getInstance().currentUser
    lateinit var sharePrefer : SharePrefer
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
                var user : User = p0.getValue(User ::class.java)!!
                nameUserProfile.text = user.username
                Glide.with(this@ProfileFragment)
                        .load(user.avatarURL)
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatarUserProfile)
                emailUserProfile.text = currentUser!!.email
            }

        })








        logoutBtn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            sharePrefer.setStatus(false)
            var intent = Intent(context,LoginActivity ::class.java)
            startActivity(intent)
        }
    }


}