package com.pelutime.ui.main.admin.ubication

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.firestore.GeoPoint
import com.pelutime.data.model.main.ubication.Ubication
import com.pelutime.application.State
import com.pelutime.databinding.FragmentAdminUbicationBinding
import com.pelutime.presentation.main.admin.ubication.AdminUbicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AdminUbicationFragment : Fragment() {

    // VIEW BINDING
    private var _binding: FragmentAdminUbicationBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<AdminUbicationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminUbicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBackPressed()
        observerGetUbication()
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
        binding.editPhone.isEnabled = true
        binding.editWebsite.isEnabled = true
        binding.editName.isFocusableInTouchMode = true
        binding.editPhone.isFocusableInTouchMode = true
        binding.editWebsite.isFocusableInTouchMode = true
    }

    private fun setTextsDisabled() {
        binding.editName.isEnabled = false
        binding.editPhone.isEnabled = false
        binding.editWebsite.isEnabled = false
        binding.editName.isFocusableInTouchMode = false
        binding.editPhone.isFocusableInTouchMode = false
        binding.editWebsite.isFocusableInTouchMode = false
        binding.editName.isFocusable = false
        binding.editPhone.isFocusable = false
        binding.editWebsite.isFocusable = false
    }

    private fun setViewsEnabled() {
        binding.imageUbication.isEnabled = true
        binding.layoutChange.isEnabled = true
        binding.cardModify.isEnabled = true
        binding.cardSave.isEnabled = true
    }

    private fun setViewsDisabled() {
        binding.imageUbication.isEnabled = false
        binding.layoutChange.isEnabled = false
        binding.cardModify.isEnabled = false
        binding.cardSave.isEnabled = false
    }

    private fun observerGetUbication() {
        viewModel.liveGetUbication.observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    binding.layoutProgress.visibility = View.GONE
                    binding.layoutUbication.visibility = View.VISIBLE
                    setUbicationData(result.data)
                    setEventsButtons(result.data.image)
                }
                is State.Failure -> {
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE GET UBICATION", result.exception.toString())

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

    private fun setUbicationData(ubication: Ubication) {
        binding.editName.setText(ubication.name)
        binding.editAddress.setText(ubication.address)
        binding.editSchedule.setText(ubication.schedule)
        binding.editPhone.setText(ubication.phone)
        binding.editWebsite.setText(ubication.website)

        binding.cardImage.visibility = View.VISIBLE
        Glide.with(requireContext()).load(ubication.image).into(binding.imageUbication)
    }

    private fun setEventsButtons(image: String) {
        binding.cardModify.setOnClickListener {
            setTextsEnabled()
            setViewsDisabled()
            binding.cardModify.isEnabled = true
            binding.cardSave.isEnabled = true
            binding.cardModify.visibility = View.INVISIBLE
            binding.cardSave.visibility = View.VISIBLE
            binding.editAddress.setTextColor(Color.parseColor("#606060"))
            binding.editSchedule.setTextColor(Color.parseColor("#606060"))
            binding.editName.requestFocusFromTouch()
        }
        binding.cardSave.setOnClickListener {
            if (binding.editName.text.isNotEmpty() && binding.editPhone.text.length > 7 && binding.editWebsite.text.length > 12 &&
                binding.editWebsite.text.contains(".") && binding.editWebsite.text.contains("www")) {

                val local = Ubication(
                    address = "",
                    admin = "",
                    geopoint = GeoPoint(0.00,0.00),
                    image = "",
                    name = "${binding.editName.text}",
                    phone = "${binding.editPhone.text}",
                    place = "",
                    schedule = "",
                    website = "${binding.editWebsite.text}"
                )

                observerUpdateUbication(local)

            } else {
                Toast.makeText(context, "Por favor, revise los datos ingresados!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.imageUbication.setOnClickListener {
            binding.layoutImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(image).into(binding.photoView)
        }
        binding.imageClose.setOnClickListener {
            binding.layoutImage.visibility = View.INVISIBLE
        }
        binding.layoutChange.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)
            }
        }
        binding.editPhone.setOnClickListener {
            if (binding.cardModify.visibility == View.VISIBLE) {
                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${binding.editPhone.text}")
                    startActivity(this)
                }
            }
        }
        binding.editWebsite.setOnClickListener {
            if (binding.cardModify.visibility == View.VISIBLE) {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://${binding.editWebsite.text.toString().replace("https://", "")}")
                    startActivity(this)
                }
            }
        }
    }

    private fun observerUpdateUbication(ubication: Ubication) {
        viewModel.liveUpdateUbication(ubication).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setTextsDisabled()
                    setViewsDisabled()
                    binding.cardSave.visibility = View.INVISIBLE
                    binding.cardModify.visibility = View.VISIBLE
                    binding.layoutProgress.visibility = View.VISIBLE
                    binding.editAddress.setTextColor(Color.parseColor("#222222"))
                    binding.editSchedule.setTextColor(Color.parseColor("#222222"))
                }
                is State.Success -> {
                    var counter = 0
                    object : CountDownTimer(2000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            counter++
                            binding.layoutProgress.visibility = View.GONE
                            binding.layoutDone.visibility = View.VISIBLE
                        }
                        override fun onFinish() {
                            setViewsEnabled()
                            binding.layoutDone.visibility = View.GONE
                        }
                    }.start()
                }
                is State.Failure -> {
                    setTextsEnabled()
                    setViewsEnabled()
                    binding.editAddress.setTextColor(Color.parseColor("#606060"))
                    binding.editSchedule.setTextColor(Color.parseColor("#606060"))
                    binding.layoutProgress.visibility = View.GONE

                    Log.e("FIRESTORE UPD UBICATION", result.exception.toString())

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            binding.layoutProgress.visibility = View.VISIBLE
            observerUpdateImage(data?.data!!)
        }
    }

    private fun observerUpdateImage(image: Uri) {
        viewModel.liveUpdateImage(image, "Place.png").observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    setViewsEnabled()
                    setEventsButtons(result.data)
                    Glide.with(requireContext()).load(result.data).into(binding.imageUbication)
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
}