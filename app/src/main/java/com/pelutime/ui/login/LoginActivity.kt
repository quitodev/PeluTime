package com.pelutime.ui.login

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.pelutime.BuildConfig
import com.pelutime.application.State
import com.pelutime.data.model.login.Login
import com.pelutime.databinding.ActivityLoginBinding
import com.pelutime.presentation.login.LoginViewModel
import com.pelutime.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class LoginActivity : AppCompatActivity() {

    // VIEW BINDING
    private lateinit var binding: ActivityLoginBinding

    // VIEW MODEL
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutSplash.visibility = View.VISIBLE
        binding.layoutLogin.visibility = View.INVISIBLE

        observerSignIn("", "")
        setEventsButtons()
    }

    private fun setEventsButtons() {
        binding.cardSignIn.setOnClickListener {
            loginUser()
        }
        binding.cardSignUp.setOnClickListener {
            if (binding.cardBack.visibility == View.VISIBLE) {
                createUser()
            }

            binding.cardSignIn.visibility = View.GONE
            binding.cardSendEmail.visibility = View.GONE
            binding.textForgetPass.visibility = View.GONE
            binding.cardSignUp.setCardBackgroundColor(Color.parseColor("#E91E63"))
            binding.textSignUp.setTextColor(Color.parseColor("#EFEFEF"))
            binding.cardBack.visibility = View.VISIBLE
            binding.textName.visibility = View.VISIBLE
            binding.editName.visibility = View.VISIBLE
            binding.textPass.visibility = View.VISIBLE
            binding.editPass.visibility = View.VISIBLE
            binding.layoutCheckTerms.visibility = View.VISIBLE
            binding.textForgetPass.visibility = View.GONE
            binding.editName.requestFocusFromTouch()
        }
        binding.cardBack.setOnClickListener {
            binding.cardSignIn.visibility = View.VISIBLE
            binding.textForgetPass.visibility = View.VISIBLE
            binding.cardSignUp.setCardBackgroundColor(Color.parseColor("#EFEFEF"))
            binding.textSignUp.setTextColor(Color.parseColor("#E91E63"))
            binding.cardBack.visibility = View.GONE
            binding.textName.visibility = View.GONE
            binding.editName.visibility = View.GONE
            binding.layoutCheckTerms.visibility = View.GONE
            binding.editEmail.requestFocusFromTouch()
        }
        binding.textForgetPass.setOnClickListener {
            binding.textForgetPass.visibility = View.GONE
            binding.cardSendEmail.visibility = View.VISIBLE
            binding.cardSignIn.visibility = View.GONE
            binding.textName.visibility = View.GONE
            binding.editName.visibility = View.GONE
            binding.textPass.visibility = View.GONE
            binding.editPass.visibility = View.GONE
            binding.layoutCheckTerms.visibility = View.GONE
            binding.editEmail.requestFocusFromTouch()
        }
        binding.cardSendEmail.setOnClickListener {
            recoverPass()
        }
        binding.textTerms.setOnClickListener {
            binding.layoutTerms.visibility = View.VISIBLE
        }
        binding.cardCloseTerms.setOnClickListener {
            binding.layoutTerms.visibility = View.GONE
        }
        binding.textHelp.setOnClickListener {
            Toast.makeText(this@LoginActivity,
                "Cualquier duda o inconveniente, por favor envíenos un mail a soportepelutime@gmail.com y le responderemos a la brevedad!",
                Toast.LENGTH_LONG).show()
        }
    }

    private fun recoverPass() {
        val email = binding.editEmail.text.toString()

        if (email.contains("@") && email.contains(".") && email.length > 12) {
            observerSendPassResetEmail(email)
        } else {
            Toast.makeText(this@LoginActivity, "Por favor, revise que el email ingresado sea válido!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser() {
        val email = binding.editEmail.text.toString()
        val pass = binding.editPass.text.toString()

        if (email.contains("@") && email.contains(".") && email.length > 12 && pass.length > 8) {
            observerSignIn(email, pass)
        } else {
            Toast.makeText(this@LoginActivity,
                "Por favor, revise que el email sea válido y que la contraseña contenga al menos 8 caracteres!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createUser() {
        val name = binding.editName.text.toString()
        val email = binding.editEmail.text.toString()
        val pass = binding.editPass.text.toString()

        if (name.isNotEmpty() && email.contains("@") && email.contains(".") && email.length > 12 && pass.length > 8
            && binding.checkTerms.isChecked) {

            observerSignUp(name, email, pass)
        } else {
            Toast.makeText(this@LoginActivity,
                "Por favor, revise que el nombre y el email sean válidos, que la contraseña contenga al menos 8 caracteres y que aceptó los " +
                        "términos y condiciones!", Toast.LENGTH_LONG).show()
        }
    }

    private fun observerSendPassResetEmail(email: String) {
        viewModel.liveSendPassResetEmail(email).observe(this) { result ->
            when (result) {
                is State.Loading -> {
                    binding.viewEmpty.visibility = View.VISIBLE
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    binding.viewEmpty.visibility = View.GONE
                    binding.layoutProgress.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Email enviado con éxito! Por favor, revise su bandeja de entrada.",
                        Toast.LENGTH_SHORT).show()

                    binding.textForgetPass.visibility = View.VISIBLE
                    binding.cardSendEmail.visibility = View.GONE
                    binding.cardSignIn.visibility = View.VISIBLE
                    binding.textPass.visibility = View.VISIBLE
                    binding.editPass.visibility = View.VISIBLE
                    binding.editName.requestFocusFromTouch()
                }
                is State.Failure -> {
                    binding.viewEmpty.visibility = View.GONE
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE SEND EMAIL", result.exception.toString())

                    when {
                        "There is no user record corresponding to this identifier" in result.exception.toString() -> {
                            Toast.makeText(this@LoginActivity, "Por favor, revise que el email ingresado sea correcto!",
                                Toast.LENGTH_SHORT).show()
                        }
                        "Unable to resolve host" in result.exception.toString() -> {
                            Toast.makeText(this@LoginActivity, "Por favor, revise su conexión!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun observerSignIn(email: String, pass: String) {
        viewModel.liveSignIn(email, pass).observe(this) { result ->
            when (result) {
                is State.Loading -> {
                    if (email.isNotEmpty() && pass.isNotEmpty()) {
                        binding.viewEmpty.visibility = View.VISIBLE
                        binding.layoutProgress.visibility = View.VISIBLE
                    }
                }
                is State.Success -> {
                    if (result.data.id == "Usuario desconectado") {
                        binding.layoutLogin.visibility = View.VISIBLE
                        binding.layoutSplash.visibility = View.GONE
                    } else {
                        observerCheckVersion(result.data)
                    }
                }
                is State.Failure -> {
                    Log.e("FIRESTORE SIGN IN", result.exception.toString())

                    when {
                        "There is no user record corresponding to this identifier" in result.exception.toString() -> {
                            Toast.makeText(this@LoginActivity, "Por favor, revise que el email ingresado sea correcto!",
                                Toast.LENGTH_SHORT).show()
                        }
                        "The password is invalid or the user does not have a password" in result.exception.toString() -> {
                            Toast.makeText(this@LoginActivity, "Por favor, revise que la contraseña ingresada sea correcta!",
                                Toast.LENGTH_SHORT).show()
                        }
                        "Unable to resolve host" in result.exception.toString() -> {
                            Toast.makeText(this@LoginActivity, "Por favor, revise su conexión!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }

                    if (email.isNotEmpty() && pass.isNotEmpty()) {
                        binding.viewEmpty.visibility = View.GONE
                        binding.layoutProgress.visibility = View.GONE
                    } else {
                        binding.layoutLogin.visibility = View.VISIBLE
                        binding.layoutSplash.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun observerSignUp(name: String, email: String, pass: String) {
        viewModel.liveSignUp(name, email, pass).observe(this) { result ->
            when (result) {
                is State.Loading -> {
                    binding.viewEmpty.visibility = View.VISIBLE
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    startHome(result.data.id, result.data.place)
                }
                is State.Failure -> {
                    binding.viewEmpty.visibility = View.GONE
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE SIGN UP", result.exception.toString())

                    when {
                        "The email address is already in use by another account" in result.exception.toString() -> {
                            Toast.makeText(this@LoginActivity, "Por favor, intente con otro email!", Toast.LENGTH_SHORT).show()
                        }
                        "Unable to resolve host" in result.exception.toString() -> {
                            Toast.makeText(this@LoginActivity, "Por favor, revise su conexión!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun observerCheckVersion(login: Login) {
        viewModel.liveCheckVersion.observe(this) { result ->
            when (result) {
                is State.Loading -> { }
                is State.Success -> {
                    val lastVersion = result.data
                    val userVersion = BuildConfig.VERSION_CODE
                    if (userVersion < lastVersion) {
                        showUpdateDialog(login)
                    } else {
                        startHome(login.id, login.place)
                    }
                }
                is State.Failure -> {
                    Log.e("FIRESTORE APP VERSION", result.exception.toString())
                }
            }
        }
    }

    private fun showUpdateDialog(login: Login) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Existe una nueva versión de la aplicación. Podés actualizarla ahora o más tarde!")
            .setCancelable(false)
            .setPositiveButton("Actualizar ahora") { dialog, _ ->
                openStore()
                dialog.dismiss()
            }
            .setNegativeButton("Más tarde") { dialog, _ ->
                dialog.dismiss()
                startHome(login.id, login.place)
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Actualización disponible")
        alert.show()
    }

    private fun openStore() {
        try {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=com.pelutime")
                startActivity(this)
                finish()
            }
        } catch (e: ActivityNotFoundException) {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.pelutime")
                startActivity(this)
                finish()
            }
        }
    }

    private fun startHome(userId: String, userPlace: String) {
        Intent(this, MainActivity::class.java).apply {
            putExtra("userId", userId)
            putExtra("userPlace", userPlace)
            startActivity(this)
            finish()
        }
    }
}