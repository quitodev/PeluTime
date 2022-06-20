package com.pelutime.ui.main.users.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.databinding.FragmentUsersAccountBinding
import com.pelutime.presentation.main.users.account.UsersAccountViewModel
import com.pelutime.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class UsersAccountFragment : Fragment() {

    // VIEW BINDING
    private var _binding: FragmentUsersAccountBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<UsersAccountViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsersAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed()
        observerGetUserRoom()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, true) {
            if (binding.layoutImage.visibility == View.VISIBLE) {
                binding.layoutImage.visibility = View.GONE
            } else {
                activity?.finish()
            }
        }
    }

    private fun setTextsEnabled() {
        binding.editName.isEnabled = true
        binding.editEmail.isEnabled = true
        binding.editPass.isEnabled = true
        binding.editName.isFocusableInTouchMode = true
        binding.editEmail.isFocusableInTouchMode = true
        binding.editPass.isFocusableInTouchMode = true
    }

    private fun setTextsDisabled() {
        binding.editName.isEnabled = false
        binding.editEmail.isEnabled = false
        binding.editPass.isEnabled = false
        binding.editName.isFocusableInTouchMode = false
        binding.editEmail.isFocusableInTouchMode = false
        binding.editPass.isFocusableInTouchMode = false
        binding.editName.isFocusable = false
        binding.editEmail.isFocusable = false
        binding.editPass.isFocusable = false
    }

    private fun setViewsEnabled() {
        binding.circleImage.isEnabled = true
        binding.layoutChange.isEnabled = true
        binding.textHelp.isEnabled = true
        binding.cardModify.isEnabled = true
        binding.cardSave.isEnabled = true
        binding.cardSignOut.isEnabled = true
        binding.cardDelete.isEnabled = true
    }

    private fun setViewsDisabled() {
        binding.circleImage.isEnabled = false
        binding.layoutChange.isEnabled = false
        binding.textHelp.isEnabled = false
        binding.cardModify.isEnabled = false
        binding.cardSave.isEnabled = false
        binding.cardSignOut.isEnabled = false
        binding.cardDelete.isEnabled = false
    }

    private fun setEventsButtons(image: String) {
        if (image != "Vacío") {
            binding.circleImage.setOnClickListener {
                setViewsDisabled()
                binding.layoutImage.visibility = View.VISIBLE
                Glide.with(requireContext()).load(image).into(binding.photoView)
            }
            binding.imageClose.setOnClickListener {
                setViewsEnabled()
                binding.layoutImage.visibility = View.GONE
            }
        }
        binding.layoutChange.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)
            }
        }
        binding.cardModify.setOnClickListener {
            setTextsEnabled()
            setViewsDisabled()
            binding.cardModify.isEnabled = true
            binding.cardSave.isEnabled = true
            binding.cardModify.visibility = View.INVISIBLE
            binding.cardSave.visibility = View.VISIBLE
            binding.cardSignOut.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorDisabled))
            binding.cardDelete.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorDisabled))
            binding.editName.requestFocusFromTouch()
        }
        binding.cardSave.setOnClickListener {
            val name = binding.editName.text.toString()
            val email = binding.editEmail.text.toString()
            val pass = binding.editPass.text.toString()

            viewModel.liveGetUserRoom.value!!.mapSuccess { user ->
                if (name == user.name && email == user.email && pass == "Contraseña") {
                    setTextsDisabled()
                    setViewsEnabled()
                    binding.cardModify.visibility = View.VISIBLE
                    binding.cardSave.visibility = View.INVISIBLE
                    binding.cardSignOut.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
                    binding.cardDelete.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))

                    var counter = 0
                    object : CountDownTimer(2000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            counter++
                            binding.layoutDone.visibility = View.VISIBLE
                        }
                        override fun onFinish() {
                            binding.layoutDone.visibility = View.GONE
                        }
                    }.start()

                } else {
                    if (name.isNotEmpty() && email.contains("@") && email.contains(".") && email.length > 12 && pass.length > 8) {
                        setViewsDisabled()
                        binding.layoutUpdate.visibility = View.VISIBLE

                        binding.cardConfirmUpdate.setOnClickListener {
                            if (binding.editOldPass.text.length > 8) {
                                val oldPass = binding.editOldPass.text.toString()
                                observerUpdateUser(name, email, pass, oldPass)
                            } else {
                                Toast.makeText(context, "Por favor, revise que la contraseña contenga al menos 8 caracteres!",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                        binding.cardCloseUpdate.setOnClickListener {
                            setViewsEnabled()
                            binding.layoutUpdate.visibility = View.GONE
                        }
                    } else {
                        Toast.makeText(context, "Por favor, revise que el nombre y el email sean válidos, y que la contraseña contenga al menos " +
                                "8 caracteres!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.cardDelete.setOnClickListener {
            setViewsDisabled()
            binding.layoutDelete.visibility = View.VISIBLE
        }
        binding.cardConfirmDelete.setOnClickListener {
            if (binding.editOldPassDelete.text.length > 8) {
                val oldPass = binding.editOldPassDelete.text.toString()
                observerDeleteUser(oldPass)
            } else {
                Toast.makeText(context, "Por favor, revise que la contraseña contenga al menos 8 caracteres!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.cardCloseDelete.setOnClickListener {
            setViewsEnabled()
            binding.layoutDelete.visibility = View.GONE
        }
        binding.cardSignOut.setOnClickListener {
            observerSignOut()
        }
        binding.textHelp.setOnClickListener {
            Toast.makeText(context, "Cualquier duda o inconveniente, por favor envíenos un mail a soportepelutime@gmail.com y le responderemos " +
                    "a la brevedad!", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observerGetUserRoom() {
        viewModel.liveGetUserRoom.observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> { }
                is State.Success -> {
                    binding.editName.setText(result.data.name)
                    binding.editEmail.setText(result.data.email)
                    binding.editPass.setText("Contraseña")

                    if (result.data.image == "Vacío") {
                        binding.circleImage.setBackgroundResource(R.drawable.ic_logo)
                    } else {
                        Glide.with(requireContext()).load(result.data.image).into(binding.circleImage)
                    }

                    setEventsButtons(result.data.image)
                }
                is State.Failure -> {
                    Log.e("ROOM GET NAME", result.exception.toString())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            observerUpdateImage(data?.data!!)
        }
    }

    private fun observerUpdateImage(image: Uri) {
        viewModel.liveUpdateImage(image).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    setViewsEnabled()
                    setEventsButtons(result.data)
                    Glide.with(requireContext()).load(result.data).into(binding.circleImage)
                    binding.layoutProgress.visibility = View.GONE
                }
                is State.Failure -> {
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE IMAGE", result.exception.toString())

                    when {
                        "User does not have permission to access this object" in result.exception.toString() -> {
                            Toast.makeText(context, "Por favor, revise que la imagen esté en formato PNG o JPEG y no supere los 5 MB!",
                                Toast.LENGTH_LONG).show()
                        }
                        "Unable to resolve host" in result.exception.toString() -> {
                            Toast.makeText(context, "Por favor, revise su conexión!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun observerUpdateUser(name: String, email: String, pass: String, oldPass: String) {
        viewModel.liveUpdateUser(name, email, pass, oldPass).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setTextsDisabled()
                    setViewsDisabled()
                    binding.editOldPass.isEnabled = false
                    binding.editOldPass.isFocusableInTouchMode = false
                    binding.editOldPass.isFocusable = false
                    binding.cardConfirmUpdate.isEnabled = false
                    binding.cardCloseUpdate.isEnabled = false
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    when (result.data) {
                        "Usuario actualizado con éxito" -> {
                            var counter = 0
                            object : CountDownTimer(2000, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    counter++
                                    binding.layoutProgress.visibility = View.GONE
                                    binding.layoutDone.visibility = View.VISIBLE
                                }
                                override fun onFinish() {
                                    setViewsEnabled()
                                    binding.layoutUpdate.visibility = View.GONE
                                    binding.layoutDone.visibility = View.GONE
                                    binding.cardModify.visibility = View.VISIBLE
                                    binding.cardSave.visibility = View.INVISIBLE
                                    binding.cardSignOut.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
                                    binding.cardDelete.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
                                }
                            }.start()
                        }
                        "No se pudo loguear nuevamente" -> {
                            setViewsEnabled()
                            binding.editOldPass.isEnabled = true
                            binding.editOldPass.isFocusableInTouchMode = true
                            binding.cardConfirmUpdate.isEnabled = true
                            binding.cardCloseUpdate.isEnabled = true
                            binding.layoutProgress.visibility = View.GONE
                            Toast.makeText(context, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
                is State.Failure -> {
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE UPDATE", result.exception.toString())

                    when {
                        "The password is invalid or the user does not have a password" in result.exception.toString() -> {
                            Toast.makeText(context, "Por favor, revise que la contraseña ingresada sea correcta!",
                                Toast.LENGTH_SHORT).show()
                            binding.editOldPass.isEnabled = true
                            binding.editOldPass.isFocusableInTouchMode = true
                            binding.cardConfirmUpdate.isEnabled = true
                            binding.cardCloseUpdate.isEnabled = true
                        }
                        "The email address is already in use by another account" in result.exception.toString() -> {
                            Toast.makeText(context, "Por favor, intente con otro email!", Toast.LENGTH_SHORT).show()
                            setTextsEnabled()
                            setViewsEnabled()
                            binding.layoutUpdate.visibility = View.GONE
                        }
                        "Unable to resolve host" in result.exception.toString() -> {
                            Toast.makeText(context, "Por favor, revise su conexión!", Toast.LENGTH_SHORT).show()
                            binding.editOldPass.isEnabled = true
                            binding.editOldPass.isFocusableInTouchMode = true
                            binding.cardConfirmUpdate.isEnabled = true
                            binding.cardCloseUpdate.isEnabled = true
                        }
                        else -> {
                            Toast.makeText(context, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                            binding.editOldPass.isEnabled = true
                            binding.editOldPass.isFocusableInTouchMode = true
                            binding.cardConfirmUpdate.isEnabled = true
                            binding.cardCloseUpdate.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    private fun observerDeleteUser(oldPass: String) {
        viewModel.liveDeleteUser(oldPass).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.editOldPassDelete.isEnabled = false
                    binding.editOldPassDelete.isFocusableInTouchMode = false
                    binding.editOldPassDelete.isFocusable = false
                    binding.cardConfirmDelete.isEnabled = false
                    binding.cardCloseDelete.isEnabled = false
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    when (result.data) {
                        "Usuario eliminado con éxito" -> {
                            var counter = 0
                            object : CountDownTimer(2000, 1000) {
                                override fun onTick(millisUntilFinished: Long) {
                                    counter++
                                    binding.layoutProgress.visibility = View.GONE
                                    binding.layoutDone.visibility = View.VISIBLE
                                }
                                override fun onFinish() {
                                    binding.layoutDone.visibility = View.GONE
                                    binding.layoutDelete.visibility = View.INVISIBLE

                                    Intent(context, LoginActivity::class.java).apply {
                                        startActivity(this)
                                        activity?.finish()
                                    }
                                }
                            }.start()
                        }
                        "No se pudo loguear nuevamente" -> {
                            setViewsEnabled()
                            binding.editOldPassDelete.isEnabled = true
                            binding.editOldPassDelete.isFocusableInTouchMode = true
                            binding.cardConfirmDelete.isEnabled = true
                            binding.cardCloseDelete.isEnabled = true
                            binding.layoutProgress.visibility = View.GONE
                            Toast.makeText(context, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
                is State.Failure -> {
                    setViewsEnabled()
                    binding.editOldPassDelete.isEnabled = true
                    binding.editOldPassDelete.isFocusableInTouchMode = true
                    binding.cardConfirmDelete.isEnabled = true
                    binding.cardCloseDelete.isEnabled = true
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE DELETE", result.exception.toString())

                    when {
                        "The password is invalid or the user does not have a password" in result.exception.toString() -> {
                            Toast.makeText(context, "Por favor, revise que la contraseña ingresada sea correcta!",
                                Toast.LENGTH_SHORT).show()
                        }
                        "Unable to resolve host" in result.exception.toString() -> {
                            Toast.makeText(context, "Por favor, revise su conexión!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun observerSignOut() {
        viewModel.liveSignOut.observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    Intent(context, LoginActivity::class.java).apply {
                        startActivity(this)
                        activity?.finish()
                    }
                }
                is State.Failure -> {
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE SIGN OUT", result.exception.toString())

                    when {
                        "Unable to resolve host" in result.exception.toString() -> {
                            Toast.makeText(context, "Por favor, revise su conexión!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "Problemas de conexión! Si continúa, escríbanos a soportepelutime@gmail.com",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}