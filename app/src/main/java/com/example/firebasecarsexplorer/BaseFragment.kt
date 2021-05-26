package com.example.firebasecarsexplorer

import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.firebasecarsexplorer.activities.MainActivity

abstract class BaseFragment : Fragment() {
    protected fun startApp(){
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
}