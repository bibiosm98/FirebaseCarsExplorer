package com.example.firebasecarsexplorer.ui.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasecarsexplorer.R
import com.example.firebasecarsexplorer.data.Car
import com.example.firebasecarsexplorer.data.User
import com.example.firebasecarsexplorer.databinding.ProfileFragmentBinding
import com.example.firebasecarsexplorer.ui.home.CarAdapter
import com.example.firebasecarsexplorer.ui.home.OnCarItemLongClick

class ProfileFragment : Fragment(), OnCarItemLongClick {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private val PROFILE_DEBUG = "PROFILE_DEBUG"
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
    override fun onCarItemLongClick(car: Car, position: Int) {
        viewModel.removeFavCar(car)
        adapter.removeCar(car, position)
    }

    private fun bindUserData(user: User){
        Log.d(PROFILE_DEBUG, user.toString())
        binding.userNameET.setText(user.name)
        binding.userSurnameET.setText(user.surname)
        binding.userEmailET.setText(user.email)
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

}