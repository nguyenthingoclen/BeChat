package com.example.bechat.ui.userlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bechat.R
import com.example.bechat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment :Fragment(){

    var userList = mutableListOf<User>()
    lateinit var adapter : AdapterUserList
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user,container,false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUsers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AdapterUserList(userList, context!!)
        userRecyclerView.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        userRecyclerView.adapter = adapter
        adapter.onClickItemListenner(object :AdapterUserList.OnClickItemListener{
            override fun onItemClick(user: User) {
                var bundle = Bundle()
                bundle.putString("user",user.id)
                view.findNavController().navigate(R.id.chatDetailFragment,bundle)
            }

        })
        imgSettingUser.setOnClickListener {
           // FirebaseAuth.getInstance().signOut()

        }
    }
    private fun getUsers(){
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var databaseReference :DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                userList.clear()
                for (dataSnapshot in p0.children){
                    var user = dataSnapshot.getValue(User::class.java)
                    if (!user?.id.equals(firebaseUser?.uid)){
                        userList.add(user!!)
                    }

                }
                adapter.notifyDataSetChanged()
            }

        })
    }
}