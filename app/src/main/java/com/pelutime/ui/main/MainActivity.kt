package com.pelutime.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.pelutime.R
import com.pelutime.application.State
import com.pelutime.databinding.ActivityMainBinding
import com.pelutime.presentation.main.MainViewModel
import com.pelutime.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    // VIEW BINDING
    private lateinit var binding: ActivityMainBinding

    // VIEW MODEL
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEventsButtons()
        checkSelectedPlace()
    }

    private fun setEventsButtons() {
        binding.cardSignOut.setOnClickListener {
            observerSignOut()
        }
    }

    private fun checkSelectedPlace() {
        if (intent.extras != null) {
            if (intent.getStringExtra("userPlace") == "Vacío") {
                binding.layoutMaps.visibility = View.VISIBLE
                setNavigationMaps()
            } else {
                observerCheckUser()
            }
        }
    }

    private fun observerCheckUser() {
        viewModel.liveCheckUser(intent.getStringExtra("userId")!!).observe(this) { result ->
            when (result) {
                is State.Loading -> {
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    binding.layoutProgress.visibility = View.GONE

                    if (result.data == "Admin") {
                        binding.layoutAdmin.visibility = View.VISIBLE
                        setNavigationAdmin()
                    } else {
                        binding.layoutSuperAdmin.visibility = View.VISIBLE
                    }
                }
                is State.Failure -> {
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE CHECK USER", result.exception.toString())

                    when {
                        "NOT_FOUND: No document to update" in result.exception.toString() -> {
                            binding.layoutUsers.visibility = View.VISIBLE
                            setNavigationUsers()
                        }
                        "Unable to resolve host" in result.exception.toString() -> {
                            Toast.makeText(this@MainActivity, "Por favor, revise su conexión!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@MainActivity, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun observerSignOut() {
        viewModel.liveSignOut.observe(this) { result ->
            when (result) {
                is State.Loading -> { }
                is State.Success -> {
                    Intent(this, LoginActivity::class.java).apply {
                        startActivity(this)
                        finish()
                    }
                }
                is State.Failure -> {
                    Log.e("FIRESTORE SIGN OUT", result.exception.toString())

                    when {
                        "Unable to resolve host" in result.exception.toString() -> {
                            Toast.makeText(this@MainActivity, "Por favor, revise su conexión!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@MainActivity, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun setNavigationMaps() {
        Navigation.findNavController(this, R.id.fragmentMaps)
    }

    private fun setNavigationAdmin() {
        NavigationUI.setupWithNavController(binding.bottomNavMenuAdmin, Navigation.findNavController(this, R.id.fragmentAdmin))
    }

    private fun setNavigationUsers() {
        NavigationUI.setupWithNavController(binding.bottomNavMenuUsers, Navigation.findNavController(this, R.id.fragmentUsers))
    }
}