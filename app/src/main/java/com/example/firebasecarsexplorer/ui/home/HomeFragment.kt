package com.example.firebasecarsexplorer.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasecarsexplorer.R
import com.example.firebasecarsexplorer.data.Car
import com.example.firebasecarsexplorer.databinding.FragmentHomeBinding
import com.example.firebasecarsexplorer.repository.FirebaseRepository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), OnCarItemLongClick {

    private val HOME_DEBUG = "HOME_DEBUG"
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter = CarAdapter(this)
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.logout_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout_action -> {
                auth.signOut()
                requireActivity().finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeViewModel.cars.observe(viewLifecycleOwner, { list ->
            adapter.setCar(list)
        })
    }

    override fun onCarItemLongClick(car: Car, position: Int) {
        Log.d(HOME_DEBUG, car.toString())
//        Toast.makeText(requireContext(), car.name, Toast.LENGTH_SHORT).show()
        car.name?.let {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
            homeViewModel.addFavCar(car)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}