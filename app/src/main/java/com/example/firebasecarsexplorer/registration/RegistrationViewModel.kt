package com.example.firebasecarsexplorer.registration

import androidx.lifecycle.ViewModel
import com.example.firebasecarsexplorer.data.User
import com.example.firebasecarsexplorer.repository.FirebaseRepository


class RegistrationViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    fun createNewUser(user: User){
        repository.createNewUser(user)
    }
}