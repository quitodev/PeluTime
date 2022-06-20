package com.pelutime.ui.main.users.home.dialogs

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
import com.pelutime.application.State
import com.pelutime.data.model.notifications.NotificationData
import com.pelutime.databinding.FragmentUsersHomeDeleteDialogBinding
import com.pelutime.presentation.main.users.home.UsersHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class UsersHomeDeleteFragment : DialogFragment() {

    // VIEW BINDING
    private var _binding: FragmentUsersHomeDeleteDialogBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<UsersHomeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentUsersHomeDeleteDialogBinding.inflate(inflater, container, false)
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
        binding.editSchedule.text = arguments?.getString("schedule")
        binding.textSection.text = arguments?.getString("sectionSelected")
    }

    private fun setEventsButtons() {
        binding.cardConfirm.setOnClickListener {
            val document = viewModel.getDocument(arguments?.getString("name")!!, arguments?.getString("schedule")!!)
            observerUpdateToFree(document[0], document[1])
        }

        binding.cardClose.setOnClickListener { dismiss() }
    }

    private fun observerUpdateToFree(date: String, hour: String) {
        viewModel.liveUpdateToFree(date, hour).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    if (result.data == "Documento actualizado con éxito") {
                        checkStatusBooking()
                    } else {
                        setViewsEnabled()
                        binding.layoutProgress.visibility = View.GONE
                        Toast.makeText(context, "Límite de reservas y/o cancelaciones alcanzado!", Toast.LENGTH_SHORT).show()
                    }
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

    private fun checkStatusBooking() {
        if (arguments?.getString("status") == "RESERVADO") {
            NotificationData("Cancelaron un turno", "Alguien canceló un turno recientemente!").also {
                observerTokenNotification(it)
            }
        } else {
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