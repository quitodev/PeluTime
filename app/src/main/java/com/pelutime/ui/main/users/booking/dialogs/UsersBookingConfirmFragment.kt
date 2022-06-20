package com.pelutime.ui.main.users.booking.dialogs

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
import com.pelutime.databinding.FragmentUsersBookingConfirmDialogBinding
import com.pelutime.presentation.main.users.booking.UsersBookingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class UsersBookingConfirmFragment : DialogFragment() {

    // VIEW BINDING
    private var _binding: FragmentUsersBookingConfirmDialogBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<UsersBookingViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentUsersBookingConfirmDialogBinding.inflate(inflater, container, false)
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
    }

    private fun setViewsDisabled() {
        binding.cardConfirm.isEnabled = false
        binding.cardClose.isEnabled = false
    }

    private fun setUserData() {
        if (arguments?.getString("image") == "Vacío") {
            binding.circleImage.setImageResource(R.drawable.ic_logo)
        } else {
            Glide.with(requireContext()).load(arguments?.getString("image")).into(binding.circleImage)
        }
        binding.editName.text = arguments?.getString("name")
        binding.textSection.text = arguments?.getString("section")
        binding.editSchedule.text = arguments?.getString("schedule")
    }

    private fun setEventsButtons() {
        binding.cardConfirm.setOnClickListener {
            val document = viewModel.getDocument(arguments?.getString("name")!!, arguments?.getString("schedule")!!)

            val map = mutableMapOf(
                "id" to "",
                "date" to document[0],
                "hour" to document[1],
                "name" to "",
                "section" to arguments?.getString("section")!!
            )

            observerUpdateToPending(map)
        }

        binding.cardClose.setOnClickListener { dismiss() }
    }

    private fun observerUpdateToPending(map: MutableMap<String, String>) {
        viewModel.liveUpdateToPending(map).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    if (result.data == "Documento actualizado con éxito") {
                        NotificationData("Tenés turnos por confirmar", "Alguien reservó un turno recientemente!").also {
                            observerTokenNotification(it)
                        }
                    } else {
                        setViewsEnabled()
                        binding.layoutProgress.visibility = View.GONE
                        Toast.makeText(context, "Límite de reservas y/o cancelaciones alcanzado!", Toast.LENGTH_SHORT).show()
                    }
                }
                is State.Failure -> {
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE PENDING", result.exception.toString())

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
        viewModel.liveSendNotification(notificationData).observe(viewLifecycleOwner) { result ->
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