package com.rajpakhurde.firebasedemo.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rajpakhurde.firebasedemo.data.NODE_USER
import com.rajpakhurde.firebasedemo.data.User

class UserViewModel: ViewModel() {

    private val dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USER)

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
    get() = _result

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
    get() = _users

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
    get() = _user

    fun addUser (user: User) {
        user.id = dbUsers.push().key
        dbUsers.child(user.id!!).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _result.value = null
            } else {
                _result.value = task.exception
            }
        }
    }

    fun fetchUser() {
        dbUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users = mutableListOf<User>()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        user?.id = userSnapshot.key
                        user?.let {
                            users.add(it)
                        }
                    }
                    _users.value = users
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private val childEventListener = object : ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val user = snapshot.getValue(User::class.java)
            user?.id = snapshot.key
            _user.value = user!!
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }

    fun getRealTimeUpdates() {
        dbUsers.addChildEventListener(childEventListener)
    }

    override fun onCleared() {
        super.onCleared()
        dbUsers.removeEventListener(childEventListener)
    }

}