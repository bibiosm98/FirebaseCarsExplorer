package com.example.firebasecarsexplorer.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.firebasecarsexplorer.R
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private val fbAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val navController = findNavController(R.id.nav_registration_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.login_fragment, R.id.registration_fragment
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_registration_fragment)
        return navController.navigateUp()
    }

    override fun onStart() {
        super.onStart()
        isCurrentUser()
    }

    private fun isCurrentUser() {
        fbAuth.currentUser?.let{ auth ->
           val intent = Intent(applicationContext, MainActivity::class.java).apply {
               flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
           }
            startActivity(intent)

        }
    }
}