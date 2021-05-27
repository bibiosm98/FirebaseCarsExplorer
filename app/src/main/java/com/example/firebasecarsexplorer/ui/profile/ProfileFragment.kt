package com.example.firebasecarsexplorer.ui.profile

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.firebasecarsexplorer.R
import com.example.firebasecarsexplorer.data.Car
import com.example.firebasecarsexplorer.data.User
import com.example.firebasecarsexplorer.databinding.ProfileFragmentBinding
import com.example.firebasecarsexplorer.ui.home.CarAdapter
import com.example.firebasecarsexplorer.ui.home.OnCarItemLongClick
import java.io.ByteArrayOutputStream
import java.lang.Exception

class ProfileFragment : Fragment(), OnCarItemLongClick {

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

    fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as Bitmap

            Log.d("onActivityResult", imageBitmap.byteCount.toString())
            Glide.with(this)
                .asBitmap()
                .load(imageBitmap)
                .circleCrop()
                .into(binding.userImage)

            val stream = ByteArrayOutputStream()
            val results = imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            if(results) viewModel.uploadUserPhoto(byteArray)
        }
    }
}