package com.pelutime.ui.main.users.home.dialogs

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.data.model.notifications.NotificationData
import com.pelutime.application.State
import com.pelutime.databinding.FragmentUsersHomeConfirmDialogBinding
import com.pelutime.presentation.main.users.home.UsersHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class UsersHomeConfirmFragment : DialogFragment() {

    // VIEW BINDING
    private var _binding: FragmentUsersHomeConfirmDialogBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<UsersHomeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentUsersHomeConfirmDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUserData()
        setSectionsButtons()
        setEventsButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEnabledTrueViews() {
        binding.cardConfirm.isEnabled = true
        binding.cardClose.isEnabled = true
    }

    private fun setEnabledFalseViews() {
        binding.cardConfirm.isEnabled = false
        binding.cardClose.isEnabled = false
    }

    private fun setUserData() {
        if (arguments?.getString("image") == "Vacío") {
            binding.circleImage.setImageResource(R.drawable.ic_logo)
        } else {
            Glide.with(requireContext()).load(arguments?.getString("image")).into(binding.circleImage)
        }
        binding.editName.text = arguments?.getString("nameEmployee")
        binding.editSchedule.text = arguments?.getString("schedule")
    }

    private fun setSectionsButtons() {
        val sections = arguments?.getString("sections")

        if (sections!!.contains("Cortes y peinados")) {
            binding.cardCutsOn.isFocusable = true
            binding.cardCutsOff.isFocusable = true
            binding.cardCutsOff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))

            binding.cardCutsOff.setOnClickListener {
                binding.cardCutsOff.visibility = View.INVISIBLE
                binding.cardCutsOn.visibility = View.VISIBLE

                if (sections.contains("Tintura")) {
                    binding.cardTinctureOff.visibility = View.VISIBLE
                    binding.cardTinctureOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Tratamientos")) {
                    binding.cardTreatmentsOff.visibility = View.VISIBLE
                    binding.cardTreatmentsOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Manicura")) {
                    binding.cardManicureOff.visibility = View.VISIBLE
                    binding.cardManicureOn.visibility = View.INVISIBLE
                }
            }
            binding.cardCutsOn.setOnClickListener {
                binding.cardCutsOff.visibility = View.VISIBLE
                binding.cardCutsOn.visibility = View.INVISIBLE

                if (sections.contains("Tintura")) {
                    binding.cardTinctureOff.visibility = View.VISIBLE
                    binding.cardTinctureOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Tratamientos")) {
                    binding.cardTreatmentsOff.visibility = View.VISIBLE
                    binding.cardTreatmentsOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Manicura")) {
                    binding.cardManicureOff.visibility = View.VISIBLE
                    binding.cardManicureOn.visibility = View.INVISIBLE
                }
            }
        }

        if (sections.contains("Tintura")) {
            binding.cardTinctureOn.isFocusable = true
            binding.cardTinctureOff.isFocusable = true
            binding.cardTinctureOff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))

            binding.cardTinctureOff.setOnClickListener {
                binding.cardTinctureOff.visibility = View.INVISIBLE
                binding.cardTinctureOn.visibility = View.VISIBLE

                if (sections.contains("Cortes y peinados")) {
                    binding.cardCutsOff.visibility = View.VISIBLE
                    binding.cardCutsOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Tratamientos")) {
                    binding.cardTreatmentsOff.visibility = View.VISIBLE
                    binding.cardTreatmentsOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Manicura")) {
                    binding.cardManicureOff.visibility = View.VISIBLE
                    binding.cardManicureOn.visibility = View.INVISIBLE
                }
            }
            binding.cardTinctureOn.setOnClickListener {
                binding.cardTinctureOff.visibility = View.VISIBLE
                binding.cardTinctureOn.visibility = View.INVISIBLE

                if (sections.contains("Cortes y peinados")) {
                    binding.cardCutsOff.visibility = View.VISIBLE
                    binding.cardCutsOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Tratamientos")) {
                    binding.cardTreatmentsOff.visibility = View.VISIBLE
                    binding.cardTreatmentsOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Manicura")) {
                    binding.cardManicureOff.visibility = View.VISIBLE
                    binding.cardManicureOn.visibility = View.INVISIBLE
                }
            }
        }

        if (sections.contains("Tratamientos")) {
            binding.cardTreatmentsOn.isFocusable = true
            binding.cardTreatmentsOff.isFocusable = true
            binding.cardTreatmentsOff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))

            binding.cardTreatmentsOff.setOnClickListener {
                binding.cardTreatmentsOff.visibility = View.INVISIBLE
                binding.cardTreatmentsOn.visibility = View.VISIBLE

                if (sections.contains("Cortes y peinados")) {
                    binding.cardCutsOff.visibility = View.VISIBLE
                    binding.cardCutsOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Tintura")) {
                    binding.cardTinctureOff.visibility = View.VISIBLE
                    binding.cardTinctureOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Manicura")) {
                    binding.cardManicureOff.visibility = View.VISIBLE
                    binding.cardManicureOn.visibility = View.INVISIBLE
                }
            }
            binding.cardTreatmentsOn.setOnClickListener {
                binding.cardTreatmentsOff.visibility = View.VISIBLE
                binding.cardTreatmentsOn.visibility = View.INVISIBLE

                if (sections.contains("Cortes y peinados")) {
                    binding.cardCutsOff.visibility = View.VISIBLE
                    binding.cardCutsOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Tintura")) {
                    binding.cardTinctureOff.visibility = View.VISIBLE
                    binding.cardTinctureOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Manicura")) {
                    binding.cardManicureOff.visibility = View.VISIBLE
                    binding.cardManicureOn.visibility = View.INVISIBLE
                }
            }
        }

        if (sections.contains("Manicura")) {
            binding.cardManicureOn.isFocusable = true
            binding.cardManicureOff.isFocusable = true
            binding.cardManicureOff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))

            binding.cardManicureOff.setOnClickListener {
                binding.cardManicureOff.visibility = View.INVISIBLE
                binding.cardManicureOn.visibility = View.VISIBLE

                if (sections.contains("Cortes y peinados")) {
                    binding.cardCutsOff.visibility = View.VISIBLE
                    binding.cardCutsOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Tintura")) {
                    binding.cardTinctureOff.visibility = View.VISIBLE
                    binding.cardTinctureOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Tratamientos")) {
                    binding.cardTreatmentsOff.visibility = View.VISIBLE
                    binding.cardTreatmentsOn.visibility = View.INVISIBLE
                }
            }
            binding.cardManicureOn.setOnClickListener {
                binding.cardManicureOff.visibility = View.VISIBLE
                binding.cardManicureOn.visibility = View.INVISIBLE

                if (sections.contains("Cortes y peinados")) {
                    binding.cardCutsOff.visibility = View.VISIBLE
                    binding.cardCutsOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Tintura")) {
                    binding.cardTinctureOff.visibility = View.VISIBLE
                    binding.cardTinctureOn.visibility = View.INVISIBLE
                }
                if (sections.contains("Tratamientos")) {
                    binding.cardTreatmentsOff.visibility = View.VISIBLE
                    binding.cardTreatmentsOn.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setEventsButtons() {
        binding.cardConfirm.setOnClickListener {
            var section = ""

            if (binding.cardCutsOn.visibility == View.VISIBLE) {
                section = "Cortes y peinados"
            }
            if (binding.cardTinctureOn.visibility == View.VISIBLE) {
                section = "Tintura"
            }
            if (binding.cardTreatmentsOn.visibility == View.VISIBLE) {
                section = "Tratamientos"
            }
            if (binding.cardManicureOn.visibility == View.VISIBLE) {
                section = "Manicura"
            }

            if (section.isNotEmpty()) {
                val document = viewModel.getDocument(arguments?.getString("nameEmployee")!!, arguments?.getString("schedule")!!)

                val map = mutableMapOf(
                    "id" to arguments?.getString("id")!!,
                    "date" to document[0],
                    "hour" to document[1],
                    "name" to arguments?.getString("nameUser")!!,
                    "section" to section
                )

                observerUpdateToPending(map)

            } else {
                Toast.makeText(context, "Debe elegir una sección para continuar!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cardClose.setOnClickListener { dismiss() }
    }

    private fun observerUpdateToPending(map: Map<String, String>) {
        viewModel.liveUpdateToPending(map).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setEnabledFalseViews()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    if (result.data == "Documento actualizado con éxito") {
                        NotificationData("Tenés turnos por confirmar", "Alguien reservó un turno recientemente!").also {
                            observerTokenNotification(it)
                        }
                    } else {
                        setEnabledTrueViews()
                        binding.layoutProgress.visibility = View.GONE
                        Toast.makeText(context, "Límite de reservas y/o cancelaciones alcanzado!", Toast.LENGTH_SHORT).show()
                    }
                }
                is State.Failure -> {
                    setEnabledTrueViews()
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
                    setEnabledFalseViews()
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