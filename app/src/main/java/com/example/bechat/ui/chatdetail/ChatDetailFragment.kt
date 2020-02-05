package com.example.bechat.ui.chatdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bechat.R
import com.example.bechat.helper.Util
import com.example.bechat.model.ChatData
import com.example.bechat.model.User
import com.example.bechat.ui.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat_detail.*
import java.text.SimpleDateFormat


class ChatDetailFragment: Fragment() {
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var reference : DatabaseReference?= null
    private var receiver :User?= null
    private var user :User?= null
    var messageList = mutableListOf<ChatData>()
    lateinit var adapter : ChatDetailADapter
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).handleronBackPressed(isDetail = true)
        return inflater.inflate(R.layout.fragment_chat_detail,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).hideBottomNavigation()
        adapter = ChatDetailADapter(messageList, context!!)
        chatDetailRecyclerView.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        chatDetailRecyclerView.adapter = adapter
        getUser(currentUser!!.uid)
        nameUserDetailChatTx.text = arguments?.getString("name user")
        getMessage()
        val idReceiver = arguments?.getString("user")
        reference = FirebaseDatabase.getInstance().getReference("Users").child(idReceiver!!)
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                receiver = p0.getValue(User ::class.java)

                if (!receiver?.avatarURL.equals("default")){
                    Glide.with(context!!)
                            .load(receiver?.avatarURL)
                            .apply(RequestOptions.circleCropTransform())
                            .into(avatarUserChatDetailImg)
                }
                getMessage()
            }

        })

        messETx.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus){
                Util.hideKeyboard(context!!, messETx)
            }
        }

        sendBtn.setOnClickListener{
            var message = messETx.text.toString()
            if (message.isEmpty()){
                Toast.makeText(context,getString(R.string.input_mess),Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(message)
                messETx.setText("")
            }
        }
        backImg.setOnClickListener {
            (activity as MainActivity).showBottomNavigation()
            (activity as MainActivity).handleronBackPressed(isDetail = false)
            view.findNavController().navigateUp()
        }
    }
    private fun getUser(id :String){
        var reference = FirebaseDatabase.getInstance().getReference("Users").child(id)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                user = p0.getValue(User ::class.java)
            }

        })
    }

    private fun getMessage(){

        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference?.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                messageList.clear()
                for (item in p0.children){
                    val chat = item.getValue(ChatData::class.java)
                    if (chat?.idReceiver == currentUser?.uid && chat?.idSender == receiver?.id ||
                            chat?.idReceiver == receiver?.id && chat?.idSender == currentUser?.uid){
                        messageList.add(chat!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

        })
    }

    private fun sendMessage(message: String){
        reference = FirebaseDatabase.getInstance().reference
        Log.d("TAG:ChatDetailFragment","time: ${SimpleDateFormat("EEE DD MMM yyyy HH:mm:ss").format(java.util.Date()).toString()}")
        var hashMap = HashMap<String,String>()
        hashMap.put("idSender",currentUser!!.uid)
        receiver?.id?.let { hashMap.put("idReceiver", it) }
        hashMap.put("mess",message)
        hashMap.put("time", SimpleDateFormat("EEE DD MMM yyyy HH:mm:ss").format(java.util.Date()).toString())
        hashMap.put("type","text")

        reference?.child("Chats")?.push()?.setValue(hashMap)?.addOnCompleteListener(object :OnCompleteListener<Void>{
            override fun onComplete(p0: Task<Void>) {
                if (!isFriend()){
                    addFriend()
                }
            }
        })
    }

    private fun isFriend() :Boolean{
        Log.d("TAG:ChatDetailFragment",user?.friends.toString()+" "+user?.friends?.size)
        for (id in user?.friends!!){
            if (id == receiver?.id){
                return true
            }
        }
        return false
    }

    private fun addFriend(){
        var friendsOfUser = user?.friends
        var friendsOfReceiver = receiver?.friends

        friendsOfUser?.add(0,receiver?.id!!)
        friendsOfReceiver?.add(0,user?.id!!)

        reference = FirebaseDatabase.getInstance().getReference("Users").child(user?.id!!)
        val hashMapUser = HashMap<String,Any>()
        hashMapUser.put("friends",friendsOfUser!!)
        reference?.updateChildren(hashMapUser)

        reference = FirebaseDatabase.getInstance().getReference("Users").child(receiver?.id!!)
        val hashMapReceiver = HashMap<String,Any>()
        hashMapReceiver.put("friends",friendsOfReceiver!!)
        reference?.updateChildren(hashMapReceiver)

    }

}