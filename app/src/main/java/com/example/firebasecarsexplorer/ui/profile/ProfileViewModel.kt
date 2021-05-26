package com.example.firebasecarsexplorer.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.firebasecarsexplorer.data.Car
import com.example.firebasecarsexplorer.repository.FirebaseRepository

class ProfileViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    val user = repository.getUserData()
    val favCars = user.switchMap {
        repository.getFavCars(it.favCars)
    }
    fun removeFavCar(car: Car){
        repository.removeFavCars(car)
    }
    fun editProfileData(map: Map<String, String>){
        repository.editProfileData(map)
    }
}