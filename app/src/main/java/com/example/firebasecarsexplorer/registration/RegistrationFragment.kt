package com.example.firebasecarsexplorer.registration

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebasecarsexplorer.BaseFragment
import com.example.firebasecarsexplorer.data.User
import com.example.firebasecarsexplorer.databinding.RegistrationFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class RegistrationFragment : BaseFragment () {

    companion object {
        fun newInstance() = RegistrationFragment()
    }

    private lateinit var viewModel: RegistrationViewModel
    private lateinit var binding: RegistrationFragmentBinding
    private val fbAuth = FirebaseAuth.getInstance()
    private val REG_DEBUG = "REG_DEBUG"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RegistrationFragmentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpBtn.setOnClickListener{
            val email = binding.editTextTextLoginRegistration.text?.trim().toString()
            val password = binding.editTextTextPasswordRegistration.text?.trim().toString()
            val passwordRepeat = binding.editTextTextPasswordRegistrationRepeat.text?.trim().toString()

            if(password.equals(passwordRepeat)){
                fbAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authRes ->
                        if(authRes.user != null){
                            val user = User(
                                authRes.user!!.uid,
                                "",
                                "",
                                authRes.user!!.email,
                                listOf(),
                                ""
                            )
                            viewModel.createNewUser(user)
                            startApp()
                        }

                    }
                    .addOnFailureListener{ exc ->
                        Snackbar.make(requireView(), "Coś poszło nie tak...", Snackbar.LENGTH_SHORT).show()
                        Log.d(REG_DEBUG, exc.message.toString())
                    }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}