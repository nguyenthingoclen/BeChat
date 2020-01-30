package com.example.bechat.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bechat.R
import com.example.bechat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_view_avatar.*

class ViewProfileFragment :Fragment(){
    private var currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_avatar
                ,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser!!.uid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var user : User = p0.getValue(User ::class.java)!!
                Glide.with(this@ViewProfileFragment)
                        .load(user.avatarURL)
                        .into(avatarImg)
            }

        })
        backImg.setOnClickListener {
            view.findNavController().navigateUp()
        }
    }



}