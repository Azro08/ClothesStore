package com.example.sport_store.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.sport_store.data.model.User
import com.example.sport_store.ui.user.MainActivity
import com.example.sport_store.databinding.FragmentLoginBinding
import com.example.sport_store.ui.admin.AdminActivity
import com.example.sport_store.util.AuthManager
import com.example.sport_store.util.Constants
import com.ivkorshak.el_diaries.util.ScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var authManager: AuthManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.buttonLogin.setOnClickListener {
            if (allFieldsAreFilled()) login()
            else Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun login() {
        binding.buttonLogin.isClickable = false
        lifecycleScope.launch {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            viewModel.login(email, password)
            viewModel.loggedIn.collect { state ->
                when (state) {

                    is ScreenState.Loading -> {}
                    is ScreenState.Error -> {
                        binding.buttonLogin.isClickable = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }

                    is ScreenState.Success -> {
                        getAuthToken(state.data!!)
                    }

                }
            }
        }
    }

    private fun getAuthToken(user: User) {
        lifecycleScope.launch {
            viewModel.token.collect { token ->
                if (!token.isNullOrEmpty()) {
                    authManager.saveUer(user.email, user.id, user.role)
                    val authorization = "Bearer $token"
                    authManager.saveToken(authorization)
                    Log.d("Login", "Token: $token")
                    navToUserActivity(user.role)
                } else {
                    Log.d("Login", "Token is null")
                    binding.buttonLogin.isClickable = true
                }
            }
        }
    }

    private fun navToUserActivity(role: String) {
        if (role == Constants.USER_ROLE) navToMainActivity()
        else navToAdminActivity()
        binding.buttonLogin.isClickable = true
    }

    private fun navToAdminActivity() {
        startActivity(Intent(requireActivity(), AdminActivity::class.java))
        requireActivity().finish()
    }

    private fun navToMainActivity() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun allFieldsAreFilled(): Boolean {
        return !(binding.editTextEmail.text.toString()
            .isEmpty() || binding.editTextPassword.text.toString().isEmpty())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}