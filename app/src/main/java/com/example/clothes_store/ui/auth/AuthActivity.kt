package com.example.clothes_store.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.clothes_store.databinding.ActivityAuthBinding
import com.example.clothes_store.ui.auth.signup.SignupFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private var _binding : ActivityAuthBinding?  = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        SignupFragment()
        setViewPager()
    }

    private fun setViewPager() {

        val adapter = AuthViewPagerAdapter(this)
        binding.classroomViewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.classroomViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Login"
                1 -> "Signup"
                else -> ""
            }
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}