package com.example.firebasecarsexplorer.ui.profile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.firebasecarsexplorer.BaseFragment
import com.example.firebasecarsexplorer.data.Car
import com.example.firebasecarsexplorer.data.User
import com.example.firebasecarsexplorer.databinding.ProfileFragmentBinding
import com.example.firebasecarsexplorer.ui.home.CarAdapter
import com.example.firebasecarsexplorer.ui.home.OnCarItemLongClick
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ProfileFragment : BaseFragment(), OnCarItemLongClick {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val PROFILE_DEBUG = "PROFILE_DEBUG"
    private val REQUEST_CAPTURE_IMAGE = 123

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ProfileFragmentBinding

    private val adapter = CarAdapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ProfileFragmentBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSubmitDataClick()
        setupTakePictureClick()
        binding.profileRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.profileRecyclerView.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        viewModel.user.observe(viewLifecycleOwner, { user ->
            bindUserData(user)
        })

        viewModel.favCars.observe(viewLifecycleOwner, { list ->
            list?.let {
                adapter.setCar(list)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode == REQUEST_CAPTURE_IMAGE && requestCode == RESULT_OK){
        if(resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap

            Log.d("onActivityResult", imageBitmap.byteCount.toString())
            Glide.with(this)
                .asBitmap()
                .load(imageBitmap)
                .circleCrop()
                .into(binding.userImage)

            val stream = ByteArrayOutputStream()
            val result = imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            if(result) viewModel.uploadUserPhoto(byteArray)
        }else{

            Log.d("onActivityResult", "ELSE ? ? ?")
        }
    }

    override fun onCarItemLongClick(car: Car, position: Int) {
        viewModel.removeFavCar(car)
        adapter.removeCar(car, position)
    }

    private fun bindUserData(user: User){
        Log.d(PROFILE_DEBUG, user.toString())
        binding.userNameET.setText(user.name)
        binding.userSurnameET.setText(user.surname)
        binding.userEmailET.setText(user.email)

        Glide.with(this)
            .load(user.image)
            .circleCrop()
            .into(binding.userImage)
    }

    private fun setupSubmitDataClick(){
        binding.profileSubmitBtn.setOnClickListener{
            val name = binding.userNameET.text.trim().toString()
            val surname = binding.userSurnameET.text.trim().toString()

            val map = mapOf("name" to name, "surname" to surname)
            Log.d(PROFILE_DEBUG, map.toString())
            viewModel.editProfileData(map)
        }
    }

    private fun setupTakePictureClick(){
        binding.userImage.setOnClickListener{
            takePicture()
        }
    }

//Move whole impl to MV
    fun takePicture() {
//For Camera
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        resultLauncher.launch(intent)

//For Gallery
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

//For Camera
//            val data: Intent? = result.data
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//
//            Glide.with(this)
//                .asBitmap()
//                .load(imageBitmap)
//                .circleCrop()
//                .into(binding.userImage)
//
//            val stream = ByteArrayOutputStream()
//            val results = imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//            val byteArray = stream.toByteArray()
//
//            if(results) viewModel.uploadUserPhoto(byteArray)


// For gallery
            val data: Intent? = result.data

            Glide.with(requireContext())
                .asBitmap()
                .load(data?.data)
                .circleCrop()
                .into(binding.userImage)

            runBlocking{
                pushPhoto(data?.data.toString())
            }

        }
    }

    suspend fun pushPhoto(url: String) = withContext(Dispatchers.IO) {

            val imageBitmap = Glide.with(requireContext())
                .asBitmap()
                .load(url)
                .into(100, 100) // bad way of implementation...
                .get()

            val stream = ByteArrayOutputStream()
            val results = imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            if(results) viewModel.uploadUserPhoto(byteArray)
        }

//Works almost fine, need to deal with black background after circleCrop()
//            val data: Intent? = result.data
//            Glide.with(requireContext())
//            .asBitmap()
//            .load(data?.data)
//            .circleCrop()
//            .into(object : CustomTarget<Bitmap>(){
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    binding.userImage.setImageBitmap(resource)
//                    val stream = ByteArrayOutputStream()
//                    val results = resource.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                    val byteArray = stream.toByteArray()
//
//                    if(results) viewModel.uploadUserPhoto(byteArray)
//                }
//                override fun onLoadCleared(placeholder: Drawable?) {
//                    Snackbar.make(requireView(), "Error while adding new photo", Snackbar.LENGTH_SHORT).show()
//                }
//            })
}