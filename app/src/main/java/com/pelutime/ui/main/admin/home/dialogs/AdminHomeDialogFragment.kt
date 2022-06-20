package com.pelutime.ui.main.admin.home.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.data.model.notifications.NotificationData
import com.pelutime.application.State
import com.pelutime.databinding.FragmentAdminHomeDialogBinding
import com.pelutime.presentation.main.admin.home.AdminHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AdminHomeDialogFragment : DialogFragment() {

    // VIEW BINDING
    private var _binding: FragmentAdminHomeDialogBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<AdminHomeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentAdminHomeDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUserData()
        setEventsButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setViewsEnabled() {
        binding.cardConfirm.isEnabled = true
        binding.cardClose.isEnabled = true
        binding.cardReject.isEnabled = true
    }

    private fun setViewsDisabled() {
        binding.cardConfirm.isEnabled = false
        binding.cardClose.isEnabled = false
        binding.cardReject.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        if (arguments?.getString("textButton") == "SOLO RECHAZAR") {
            binding.cardConfirm.visibility = View.GONE
            binding.textButton.text = "RECHAZAR"
            binding.textTitle.text = "Cancelar turno"
        } else {
            binding.textButton.text = arguments?.getString("textButton")
        }

        if (arguments?.getString("image") == "Vacío") {
            binding.circleImage.setImageResource(R.drawable.ic_logo)
        } else {
            Glide.with(requireContext()).load(arguments?.getString("image")).into(binding.circleImage)
        }

        binding.editName.text = arguments?.getString("nameUser")
        binding.editSchedule.text = arguments?.getString("schedule")
        binding.textSection.text = arguments?.getString("sectionSelected")
    }

    private fun setEventsButtons() {
        binding.cardConfirm.setOnClickListener {
            val document = viewModel.getDocument(arguments?.getString("nameEmployee")!!, arguments?.getString("schedule")!!)

            val map = mapOf(
                "id" to arguments?.getString("id")!!,
                "date" to document[0],
                "hour" to document[1],
                "name" to arguments?.getString("nameUser")!!,
                "section" to arguments?.getString("sectionSelected")!!
            )

            observerUpdateToBooked(map)
        }
        binding.cardReject.setOnClickListener {
            if (arguments?.getString("textButton") == "RECHAZAR" || arguments?.getString("textButton") == "SOLO RECHAZAR") {
                val document = viewModel.getDocument(arguments?.getString("nameEmployee")!!, arguments?.getString("schedule")!!)

                val map = mapOf(
                    "id" to arguments?.getString("id")!!,
                    "date" to document[0],
                    "hour" to document[1],
                    "name" to arguments?.getString("nameUser")!!,
                    "section" to arguments?.getString("sectionSelected")!!
                )

                observerUpdateToRejected(map)

            } else {
                val document = viewModel.getDocument(arguments?.getString("nameEmployee")!!, arguments?.getString("schedule")!!)

                observerUpdateToFree(document[0], document[1])
            }
        }
        binding.cardClose.setOnClickListener { dismiss() }
    }

    private fun observerUpdateToBooked(map: Map<String, String>) {
        viewModel.liveUpdateToBooked(map).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    NotificationData("Turno reservado", "El turno que reservaste quedó confirmado!").also {
                        observerTokenNotification(it)
                    }
                }
                is State.Failure -> {
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE BOOKED", result.exception.toString())

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

    private fun observerUpdateToRejected(map: Map<String, String>) {
        viewModel.liveUpdateToRejected(map).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    NotificationData("Turno cancelado", "El turno que reservaste fue cancelado!").also {
                        observerTokenNotification(it)
                    }
                }
                is State.Failure -> {
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE REJECTED", result.exception.toString())

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

    private fun observerUpdateToFree(date: String, hour: String) {
        viewModel.liveUpdateToFree(date, hour).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
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
                            binding.layoutDone.visibility = View.GONE
                            dismiss()
                        }
                    }.start()
                }
                is State.Failure -> {
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE FREE", result.exception.toString())

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

    private fun observerTokenNotification(notificationData: NotificationData) {
        viewModel.liveSendNotification(notificationData, arguments?.getString("id")!!).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
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
                            binding.layoutDone.visibility = View.GONE
                            dismiss()
                        }
                    }.start()
                }
                is State.Failure -> {
                    var counter = 0
                    object : CountDownTimer(2000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            counter++
                            binding.layoutProgress.visibility = View.GONE
                            binding.layoutDone.visibility = View.VISIBLE
                        }
                        override fun onFinish() {
                            binding.layoutDone.visibility = View.GONE
                            dismiss()
                        }
                    }.start()

                    Log.e("FIRESTORE NOTIF", result.exception.toString())
                }
            }
        }
    }
}