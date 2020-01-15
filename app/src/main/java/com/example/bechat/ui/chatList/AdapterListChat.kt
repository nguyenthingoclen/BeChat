package com.example.bechat.ui.chatList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bechat.R
import com.example.bechat.helper.Util.convertTime
import com.example.bechat.helper.Util.timeToList
import com.example.bechat.model.ChatData
import com.example.bechat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterListChat (private var list: MutableList<User>, var context: Context): RecyclerView.Adapter<AdapterListChat.ViewHolder>(),Filterable{

    var chatList : MutableList<User>
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private var mOnclickItemtListener : OnClickItemListener?= null

    init {
        chatList = list
    }
    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var key = constraint.toString()
                var filtered: MutableList<User>? = mutableListOf()
                if (key == ""){
                    filtered = list
                }else{
                    for (u in list){
                        if (u.username!!.indexOf(key)!= -1){
                            filtered?.add(u)
                        }
                    }
                }
                var result = FilterResults()
                result.count = filtered!!.size
                result.values = filtered
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                chatList = results!!.values as MutableList<User>
                notifyDataSetChanged()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.item_list_chat,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        holder.bindData(chat)
    }

    inner class ViewHolder(itemview: View) :RecyclerView.ViewHolder(itemview){
        var name = itemview.findViewById<TextView>(R.id.nameUserItemTx)
        var mess = itemview.findViewById<TextView>(R.id.contentItemTx)
        var avatar = itemview.findViewById<ImageView>(R.id.avatarUserItemImg)
        var time = itemview.findViewById<TextView>(R.id.timeMessTx)
        fun bindData(item : User){
            name.text = item.username
            Glide.with(context)
                    .load(item.avatarURL)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatar)
            //set day mess
            var reference = FirebaseDatabase.getInstance().getReference("Chats")
            reference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                   // var list = p0.children.toMutableList().sortList()
                    var list = p0.children.toMutableList()
                    Log.d("TAG:AdapterListChat","onDataChange")
                    for (i in list.indices ){
                        val chat = list[i].getValue(ChatData::class.java)
                        if (chat?.idReceiver == currentUser?.uid && chat?.idSender == item.id||
                                chat?.idReceiver == item.id && chat?.idSender == currentUser?.uid){
                            mess.text= chat?.mess
                            time.text= (chat?.time.toString()).timeToList()
                        }
                    }
                }

            })

            itemView.setOnClickListener {
                mOnclickItemtListener?.onItemClick(item)
            }
        }
    }
    private fun getUser(id :String): User?{
        var user : User?= null
        var reference = FirebaseDatabase.getInstance().getReference("Users").child(id)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                user = p0.getValue(User ::class.java)

            }

        })
        return user
    }
    fun onClickItemListenner( listen: OnClickItemListener){
        mOnclickItemtListener = listen
    }

    interface OnClickItemListener {
        fun onItemClick(user :User)
    }

    fun MutableList<DataSnapshot>.sortList(): MutableList<DataSnapshot> {
        for (i in (this.indices - 1)) {
            for (j in i+1 until  this.size){
                var chat1 = this[i].getValue(ChatData::class.java)
                var chat2 = this[j].getValue(ChatData::class.java)
                //Log.d("TAG:AdapterListChat","time: ${convertTime(chat1?.time!!.toString(), "EEE DD MMM yyyy HH:mm:ss")}")
                if (convertTime(chat1?.time!!.toString(), "EEE DD MMM yyyy HH:mm:ss") > convertTime(chat2?.time!!.toString(), "EEE DD MMM yyyy HH:mm:ss")) {
                    var temp = this[i]
                    this[i] = this[j]
                    this[j] = temp
                }
            }
        }
        for (item in this){
            var ch = item.getValue(ChatData::class.java)
            Log.d("TAG:AdapterListChat","time: ${convertTime(ch?.time!!.toString(), "EEE DD MMM yyyy HH:mm:ss")}")
        }
        return this
    }
}