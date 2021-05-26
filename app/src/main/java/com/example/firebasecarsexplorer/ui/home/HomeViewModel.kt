package com.example.firebasecarsexplorer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebasecarsexplorer.data.Car
import com.example.firebasecarsexplorer.repository.FirebaseRepository

class  HomeViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    val cars = repository.getCars()

    fun addFavCar(car: Car){
        repository.addFavCars(car )
    }
}