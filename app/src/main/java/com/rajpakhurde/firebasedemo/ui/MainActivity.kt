package com.rajpakhurde.firebasedemo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.rajpakhurde.firebasedemo.R
import com.rajpakhurde.firebasedemo.data.User
import com.rajpakhurde.firebasedemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        viewModel.result.observe(this, Observer {
            val message = if (it == null) {
                "Account Created..."
            } else {
                it.message
            }
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        })


        binding.apply {
            btnSignup.setOnClickListener {
                var fName = etFirstName.text.toString().trim()
                var lName = etLastName.text.toString().trim()
                var email = etEmail.text.toString().trim()
                var password = etPassword.text.toString().trim()
                var userMode = if (rbStudent.isChecked) {
                    "student"
                } else {
                    "mentor"
                }

                if (fName.isBlank() || lName.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(this@MainActivity,"Please fill all fields..",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val user = User()
                user.fName = fName
                user.lName = lName
                user.email = email
                user.password = password
                user.user_mode = userMode

                viewModel.addUser(user)
            }
        }

        binding.fabtnNext.setOnClickListener {
            var intent = Intent(this,FetchActivity::class.java)
            startActivity(intent)
        }
    }
}