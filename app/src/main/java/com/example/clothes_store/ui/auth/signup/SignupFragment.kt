package com.example.clothes_store.ui.auth.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.clothes_store.data.model.User
import com.example.clothes_store.databinding.FragmentSignupBinding
import com.example.clothes_store.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignupViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonSignup.setOnClickListener {
            if (!allFieldsAreFilled()) Toast.makeText(
                requireContext(),
                "Please fill all fields",
                Toast.LENGTH_SHORT
            )
                .show()
            else if (binding.editTextPassword.text.toString() != binding.editTextRePassword.text.toString())
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT)
                    .show()
            else signUp()
        }
    }

    private fun signUp() {
        lifecycleScope.launch {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val newUser = User(
                email = email,
                password = password,
                role = Constants.USER_ROLE
            )
            viewModel.signUp(newUser)
            viewModel.signedUp.collect { result ->
                if (!result.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
                }
            }
        }
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