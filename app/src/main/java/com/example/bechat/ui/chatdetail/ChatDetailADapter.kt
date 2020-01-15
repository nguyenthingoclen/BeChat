package com.example.bechat.ui.chatdetail

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bechat.R
import com.example.bechat.helper.Util.timeToDetail
import com.example.bechat.model.ChatData
import com.example.bechat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatDetailADapter (private val chatList: MutableList<ChatData>, val context: Context): RecyclerView.Adapter<ChatDetailADapter.ViewHolder>(){

    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var databaseReference : DatabaseReference?= null
    private var sender :User?= null
    private var isShowTime =false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.item_list_chat_detail,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bindData(chat)
    }

    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
        var itemLeft = itemview.findViewById<RelativeLayout>(R.id.itemLeftChat)
        var itemRight = itemview.findViewById<RelativeLayout>(R.id.itemRightChat)
        var avatarLeft = itemview.findViewById<ImageView>(R.id.avatarLeftImg)
        var messLeft = itemview.findViewById<TextView>(R.id.messLeftTx)
        var messRight = itemview.findViewById<TextView>(R.id.messRightTx)
        var timeLeft = itemview.findViewById<TextView>(R.id.timeMessLeftTx)
        var timeRight = itemview.findViewById<TextView>(R.id.timeMessRightTx)
        fun bindData(item : ChatData){
            Log.d("TAG:ChatDetailADapter","idsender: ${item.idSender} current: ${currentUser?.uid}")
            var reference = FirebaseDatabase.getInstance().getReference("Users").child(item.idSender!!)
            reference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    sender = p0.getValue(User ::class.java)
                    if(item.idSender == currentUser?.uid){

                        itemLeft.visibility = View.GONE
                        itemRight.visibility = View.VISIBLE
                        messRight.text= item.mess
                        timeRight.text = item.time?.timeToDetail()
                    }else{
                        itemLeft.visibility = View.VISIBLE
                        itemRight.visibility = View.GONE
                        messLeft.text= item.mess
                        timeLeft.text = item.time?.timeToDetail()
                        Glide.with(context)
                                .load(sender?.avatarURL)
                                .apply(RequestOptions.circleCropTransform())
                                .into(avatarLeft)
                    }
                }

            })
            itemView.setOnClickListener {
               if (isShowTime){
                   timeRight.visibility = View.GONE
                   timeLeft.visibility = View.GONE
                   isShowTime = false
               }else{
                   timeRight.visibility = View.VISIBLE
                   timeLeft.visibility = View.VISIBLE
                   isShowTime = true
               }
            }

        }
    }
    private fun getUser(id :String){
        var reference = FirebaseDatabase.getInstance().getReference("Users").child(id)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                sender = p0.getValue(User ::class.java)

            }

        })
    }
}