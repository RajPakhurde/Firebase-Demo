package com.rajpakhurde.firebasedemo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajpakhurde.firebasedemo.databinding.ActivityFetchBinding
import com.rajpakhurde.firebasedemo.vc.AgoraVcActivity

class FetchActivity : AppCompatActivity() {

    private lateinit var adapter: UsersAdapter
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: ActivityFetchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFetchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        adapter = UsersAdapter()
        binding.rvUsers.adapter = adapter
        binding.rvUsers.layoutManager = LinearLayoutManager(this)

//        viewModel.fetchUser()
        viewModel.getRealTimeUpdates()

        viewModel.user.observe(this, Observer {
            adapter.addUser(it)
        })

        viewModel.users.observe(this, Observer {
            adapter.setUsers(it)
        })

        binding.fabtnVideoCall.setOnClickListener {
            var roomId = binding.etRoomID.text.toString().trim()
            var intent = Intent(this, AgoraVcActivity::class.java)
            intent.putExtra("roomId",roomId)
            startActivity(intent)
        }

        binding.fabtnDashboard.setOnClickListener {
            var intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

    }
}