package com.rajpakhurde.firebasedemo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.rajpakhurde.firebasedemo.R
import com.rajpakhurde.firebasedemo.databinding.ActivityDashboardBinding
import com.rajpakhurde.firebasedemo.fragments.ChatFragment
import com.rajpakhurde.firebasedemo.fragments.ProfileFragments
import com.rajpakhurde.firebasedemo.fragments.SearchFragment
import com.rajpakhurde.firebasedemo.fragments.VideoFragment

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profileFragment = ProfileFragments()
        val searchFragment = SearchFragment()
        val chatFragment = ChatFragment()
        val videoFragment = VideoFragment()

        setCurrentFragment(profileFragment)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.imProfile -> setCurrentFragment(profileFragment)
                R.id.imSearch -> setCurrentFragment(searchFragment)
                R.id.imChat -> setCurrentFragment(chatFragment)
                R.id.imVideo -> setCurrentFragment(videoFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}