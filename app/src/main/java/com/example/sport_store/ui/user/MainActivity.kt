package com.example.sport_store.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sport_store.R
import com.example.sport_store.databinding.ActivityMainBinding
import com.example.sport_store.ui.admin.AdminActivity
import com.example.sport_store.ui.auth.AuthActivity
import com.example.sport_store.util.AuthManager
import com.example.sport_store.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        if (!authManager.isLoggedIn()) {
            logout()
        } else getRole()
        setBottomNavBar()
    }

    private fun getRole() {
        val userRole = authManager.getUserRole()
        if (userRole == Constants.ADMIN_ROLE) {
            Log.d("UserROle", authManager.getUserRole())
            startActivity(Intent(this, AdminActivity::class.java))
            this.finish()
        } else if (authManager.getUserRole().isEmpty()) {
            logout()
        }
    }

    private fun setBottomNavBar() {
        val navController = findNavController(R.id.nav_host)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.userHomeFragment,
                R.id.cartFragment,
                R.id.historyFragment,
                R.id.profileFragment
            )
        )

        val topLevelDestinations = setOf(
            R.id.userHomeFragment,
            R.id.cartFragment,
            R.id.historyFragment,
            R.id.profileFragment
        )
        // Show the bottom navigation view for top-level destinations only
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in topLevelDestinations) {
                binding.usersNavView.visibility = View.VISIBLE
            } else {
                binding.usersNavView.visibility = View.GONE
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.usersNavView.setupWithNavController(navController)
    }

    private fun logout() {
        startActivity(Intent(this, AuthActivity::class.java))
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}