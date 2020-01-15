package com.example.bechat.ui.userlist

import android.content.Context
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
import com.example.bechat.model.User

class AdapterUserList (private val list: MutableList<User>, var context: Context): RecyclerView.Adapter<AdapterUserList.ViewHolder>(), Filterable {


    private var mOnclickItemtListener : OnClickItemListener?= null
    var userList : MutableList<User> = list

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
                userList = results!!.values as MutableList<User>
                notifyDataSetChanged()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.item_list_user,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var user = userList[position]
        holder.bindData(user)

    }


    inner class ViewHolder(itemview: View) :RecyclerView.ViewHolder(itemview){
        var name = itemview.findViewById<TextView>(R.id.nameUserItemUserTx)
        var avatar = itemview.findViewById<ImageView>(R.id.avatarUserItemUserImg)

        fun bindData(item : User){
            name.text = item.username
            Glide.with(context)
                    .load(item.avatarURL)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatar)
            itemView.setOnClickListener {
                mOnclickItemtListener?.onItemClick(item)
            }
        }

    }

    fun onClickItemListenner( listen: OnClickItemListener){
        mOnclickItemtListener = listen
    }

    interface OnClickItemListener {
        fun onItemClick(user :User)
    }

}