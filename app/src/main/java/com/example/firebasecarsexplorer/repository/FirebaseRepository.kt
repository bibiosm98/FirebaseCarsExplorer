package com.example.firebasecarsexplorer.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firebasecarsexplorer.data.Car
import com.example.firebasecarsexplorer.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseRepository {

    private val REPO_DEBUG = "REPO_DEBUG"
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()

    fun createNewUser(user: User){
        cloud.collection("users")
            .document(user.uid!!)
            .set(user)
    }
    fun getUserData(): LiveData<User> {
        val cloudResult = MutableLiveData<User>()
        val uid = auth.currentUser?.uid

        Log.d("uid user", uid.toString())
        cloud.collection("users")
            .document(uid!!)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                cloudResult.postValue(user!!)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
        return cloudResult
    }
    fun editProfileData(map: Map<String, String>){
        cloud.collection("users")
            .document(auth.currentUser!!.uid)
            .update(map)
            .addOnSuccessListener {
                Log.d(REPO_DEBUG, "Zaktualizowano dane użytkownika")
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }
    fun uploadUserProfileImage(bytes: ByteArray){
        storage.getReference("users")
            .child("${auth.currentUser!!.uid}.jpg")
            .putBytes(bytes)
            .addOnCompleteListener{
                Log.d(REPO_DEBUG, "Wysłano zdjęcie użytkownika")
            }
            .addOnSuccessListener{
                getPhotoDownloadUrl(it.storage)
                Log.d(REPO_DEBUG, "Pobrano Url zdjęcia użytkownika")
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, "Bład wysyłania zdjęcia użytkownika: ${ it.message.toString() }")
            }
    }
    private fun getPhotoDownloadUrl(storage: StorageReference) {
        storage.downloadUrl
            .addOnSuccessListener {
                editProfileData(mapOf("image" to it.toString()))
                Log.d(REPO_DEBUG, "Edytowano zdjęcie użytkownika")
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, "Błąd edycji zdjęcia użytkownika ${ it.message.toString() }")
            }
    }

    fun getCars(): LiveData<List<Car>> {
        val cloudResult = MutableLiveData<List<Car>>()

        cloud.collection("cars")
            .get()
            .addOnSuccessListener {
                val car = it.toObjects(Car::class.java)
                cloudResult.postValue(car)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
        return cloudResult
    }
    fun getFavCars(list: List<String>?): LiveData<List<Car>>{
        val cloudResult = MutableLiveData<List<Car>>()

        if(!list.isNullOrEmpty())
            cloud.collection("cars")
                .whereIn("id", list)
                .get()
                .addOnSuccessListener {
                    val resultList = it.toObjects(Car::class.java)
                    cloudResult.postValue(resultList)
                }
                .addOnFailureListener{
                    Log.d(REPO_DEBUG, it.message.toString())
                }
        return cloudResult
    }
     fun addFavCars(car: Car){
        cloud.collection("users")
            .document(auth.currentUser?.uid!!)
            .update("favCars", FieldValue.arrayUnion(car.id))
            .addOnSuccessListener {
                Log.d(REPO_DEBUG, "Dodano do ulubionych")
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }
    fun removeFavCars(car: Car){
        cloud.collection("users")
            .document(auth.currentUser?.uid!!)
            .update("favCars", FieldValue.arrayRemove(car.id))
            .addOnSuccessListener {
                Log.d(REPO_DEBUG, "Dodano do ulubionych")
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }

}
