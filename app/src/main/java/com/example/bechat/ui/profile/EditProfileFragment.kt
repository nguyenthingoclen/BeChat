package com.example.bechat.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import com.example.bechat.R
import com.example.bechat.data.local.SharePrefer
import com.example.bechat.helper.Util
import com.example.bechat.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditProfileFragment :Fragment(){

    private var currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var sharePrefer : SharePrefer
    private lateinit var oldName :String
    private lateinit var oldPassword :String
    private var type = ""
    lateinit var reference : DatabaseReference
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_profile,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type = arguments?.getString("type")!!
        (activity as MainActivity).hideBottomNavigation()
        (activity as MainActivity).isThirdFragment = true
        sharePrefer = SharePrefer(context!!)

        if (type == "name"){
            oldName = sharePrefer.getName()!!
            newNameEtx.visibility = View.VISIBLE
            newNameEtx.setText(oldName)

        }else if (type == "password"){
            oldPassword = sharePrefer.getPassword()!!
            oldPassEtx.visibility = View.VISIBLE
            newPassEtx.visibility = View.VISIBLE
            confirmNewPassEtx.visibility = View.VISIBLE
        }




        saveBtn.setOnClickListener {
            var newName = newNameEtx.text.toString()
            var oldPassInput = oldPassEtx.text.toString()
            var newPass = newPassEtx.text.toString()
            var confirmNewPass = confirmNewPassEtx.text.toString()

            if (type == "password"){
                if (oldPassInput.isNullOrEmpty()){
                    Toast.makeText(context!!,"enter old password,please!!",Toast.LENGTH_SHORT).show()
                }else{
                    if (oldPassword == oldPassInput){
                        if (newPass.isNullOrEmpty()){
                            Toast.makeText(context!!,"enter new password,please!!",Toast.LENGTH_SHORT).show()
                        }else{
                            if (newPass == confirmNewPass){
                                currentUser?.updatePassword(newPass)?.addOnFailureListener {
                                    Toast.makeText(context!!,it.message,Toast.LENGTH_SHORT).show()
                                }?.addOnCompleteListener {
                                    Toast.makeText(context!!,"change password successful",Toast.LENGTH_SHORT).show()
                                    sharePrefer.setPassword(newPass)
                                    (activity as MainActivity).showBottomNavigation()
                                    view.findNavController().navigateUp()
                                }

                            }else Toast.makeText(context!!,"new password no match",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context!!,"password was wrong",Toast.LENGTH_SHORT).show()
                    }

                }
            }

            if (type == "name"){
                if (newName != oldName && newName.isNotEmpty()){
                    //setnewname
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser!!.uid)
                    reference.child("username").setValue(newName).addOnCompleteListener {
                        Toast.makeText(context!!,"update successful",Toast.LENGTH_SHORT).show()
                        (activity as MainActivity).showBottomNavigation()
                        view.findNavController().navigateUp()
                    }
                }
            }


        }

        toolbarEditProfile.setOnclickIconBack(View.OnClickListener {
            (activity as MainActivity).showBottomNavigation()
            view.findNavController().navigateUp()
        })

        newNameEtx.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                Util.hideKeyboard(context!!, newNameEtx)
            }
        }
        oldPassEtx.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                Util.hideKeyboard(context!!, oldPassEtx)
            }
        }
        newPassEtx.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                Util.hideKeyboard(context!!, newPassEtx)
            }
        }
        confirmNewPassEtx.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                Util.hideKeyboard(context!!, confirmNewPassEtx)
            }
        }


    }


}