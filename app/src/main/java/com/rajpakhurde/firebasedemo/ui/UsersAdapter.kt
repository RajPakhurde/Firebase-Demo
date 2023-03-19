package com.rajpakhurde.firebasedemo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rajpakhurde.firebasedemo.R
import com.rajpakhurde.firebasedemo.data.User

class UsersAdapter: RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    private var users = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item,parent,false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.fName.text = users[position].fName
        holder.lName.text = users[position].lName
        holder.userMode.text = users[position].user_mode
    }

    fun setUsers(users: List<User>) {
        this.users = users as MutableList<User>
        notifyDataSetChanged()
    }

    fun addUser(user: User){
        if (!users.contains(user)) {
            users.add(user)
            notifyDataSetChanged()
        }
    }

    class UserViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var fName = view.findViewById<TextView>(R.id.tvFname)
        var lName = view.findViewById<TextView>(R.id.tvLname)
        var userMode = view.findViewById<TextView>(R.id.tvUsermode)
    }

}