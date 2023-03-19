package com.rajpakhurde.firebasedemo.data

import com.google.firebase.database.Exclude

data class User(
    @get:Exclude
    var id: String? = null,
    var fName: String? = null,
    var lName: String? = null,
    var email: String? = null,
    var password: String? = null,
    var user_mode: String? = null,
    var headline: String? = null,
    var about: String? = null
) {
    override fun equals(other: Any?): Boolean {
         return  if (other is User) {
             other.id == id
         } else false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
