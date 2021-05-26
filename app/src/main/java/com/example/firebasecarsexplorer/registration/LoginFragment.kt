package com.example.firebasecarsexplorer.registration

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.firebasecarsexplorer.BaseFragment
import com.example.firebasecarsexplorer.databinding.LoginFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : BaseFragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private val fbAuth = FirebaseAuth.getInstance()
    private lateinit var binding: LoginFragmentBinding
    private val LOG_DEBUG = "LOG_DEBUG"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = LoginFragmentBinding.inflate(inflater)

        binding.toRegistrationBtn.setOnClickListener{
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoginOnClick()
//        setupRegistrationclick()
    }

    private fun setupLoginOnClick() {
        binding.loginBtn.setOnClickListener{
            val email = binding.editTextTextLogin.text?.trim().toString()
            val password = binding.editTextTextPassword.text?.trim().toString()
            fbAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { authRes ->
                    if(authRes.user != null) startApp()
                }
                .addOnFailureListener{ exc ->
                    Snackbar.make(requireView(), "Coś poszło nie tak...", Snackbar.LENGTH_SHORT).show()
                    Log.d(LOG_DEBUG, exc.message.toString())
                }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}