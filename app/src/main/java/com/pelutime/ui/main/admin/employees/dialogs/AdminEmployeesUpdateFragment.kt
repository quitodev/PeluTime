package com.pelutime.ui.main.admin.employees.dialogs

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.application.State
import com.pelutime.databinding.FragmentAdminEmployeesUpdateDialogBinding
import com.pelutime.presentation.main.admin.employees.AdminEmployeesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AdminEmployeesUpdateFragment : DialogFragment() {

    // VIEW BINDING
    private var _binding: FragmentAdminEmployeesUpdateDialogBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<AdminEmployeesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentAdminEmployeesUpdateDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEmployeeData()
        setEventsButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setTextsEnabled() {
        binding.editExperiences.isEnabled = true
        binding.editHobbies.isEnabled = true
        binding.editExperiences.isFocusableInTouchMode = true
        binding.editHobbies.isFocusableInTouchMode = true
    }

    private fun setTextsDisabled() {
        binding.editExperiences.isEnabled = false
        binding.editHobbies.isEnabled = false
        binding.editExperiences.isFocusableInTouchMode = false
        binding.editHobbies.isFocusableInTouchMode = false
        binding.editExperiences.isFocusable = false
        binding.editHobbies.isFocusable = false
    }

    private fun setViewsEnabled() {
        binding.circleImage.isEnabled = true
        binding.layoutChange.isEnabled = true
        binding.cardModify.isEnabled = true
        binding.cardSave.isEnabled = true
        binding.cardDelete.isEnabled = true
        binding.cardClose.isEnabled = true
    }

    private fun setViewsDisabled() {
        binding.circleImage.isEnabled = false
        binding.layoutChange.isEnabled = false
        binding.cardModify.isEnabled = false
        binding.cardSave.isEnabled = false
        binding.cardDelete.isEnabled = false
        binding.cardClose.isEnabled = false
    }

    private fun setEmployeeData() {
        val sections = arguments?.getString("sections")

        if (sections!!.contains("Cortes y peinados")) {
            binding.cardCutsOn.visibility = View.VISIBLE
            binding.cardCutsOff.visibility = View.INVISIBLE
        }
        if (sections.contains("Tintura")) {
            binding.cardTinctureOn.visibility = View.VISIBLE
            binding.cardTinctureOff.visibility = View.INVISIBLE
        }
        if (sections.contains("Tratamientos")) {
            binding.cardTreatmentsOn.visibility = View.VISIBLE
            binding.cardTreatmentsOff.visibility = View.INVISIBLE
        }
        if (sections.contains("Manicura")) {
            binding.cardManicureOn.visibility = View.VISIBLE
            binding.cardManicureOff.visibility = View.INVISIBLE
        }

        if (arguments?.getString("image") == "Vacío") {
            binding.circleImage.setImageResource(R.drawable.ic_logo)
        } else {
            Glide.with(requireContext()).load(arguments?.getString("image")).into(binding.circleImage)
        }

        binding.editName.setText(arguments?.getString("name"))
        binding.editExperiences.setText(arguments?.getString("experiences"))
        binding.editHobbies.setText(arguments?.getString("hobbies"))
    }

    private fun setSectionsButtons() {
        binding.cardCutsOff.setOnClickListener {
            binding.cardCutsOff.visibility = View.INVISIBLE
            binding.cardCutsOn.visibility = View.VISIBLE
        }
        binding.cardCutsOn.setOnClickListener {
            binding.cardCutsOff.visibility = View.VISIBLE
            binding.cardCutsOn.visibility = View.INVISIBLE
        }
        binding.cardTinctureOff.setOnClickListener {
            binding.cardTinctureOff.visibility = View.INVISIBLE
            binding.cardTinctureOn.visibility = View.VISIBLE
        }
        binding.cardTinctureOn.setOnClickListener {
            binding.cardTinctureOff.visibility = View.VISIBLE
            binding.cardTinctureOn.visibility = View.INVISIBLE
        }
        binding.cardTreatmentsOff.setOnClickListener {
            binding.cardTreatmentsOff.visibility = View.INVISIBLE
            binding.cardTreatmentsOn.visibility = View.VISIBLE
        }
        binding.cardTreatmentsOn.setOnClickListener {
            binding.cardTreatmentsOff.visibility = View.VISIBLE
            binding.cardTreatmentsOn.visibility = View.INVISIBLE
        }
        binding.cardManicureOff.setOnClickListener {
            binding.cardManicureOff.visibility = View.INVISIBLE
            binding.cardManicureOn.visibility = View.VISIBLE
        }
        binding.cardManicureOn.setOnClickListener {
            binding.cardManicureOff.visibility = View.VISIBLE
            binding.cardManicureOn.visibility = View.INVISIBLE
        }
    }

    private fun setEventsButtons() {
        binding.cardModify.setOnClickListener {
            setSectionsButtons()

            binding.cardCutsOff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            binding.cardTinctureOff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            binding.cardTreatmentsOff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
            binding.cardManicureOff.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))

            setTextsEnabled()
            setViewsDisabled()
            binding.cardModify.isEnabled = true
            binding.cardSave.isEnabled = true
            binding.cardModify.visibility = View.INVISIBLE
            binding.cardSave.visibility = View.VISIBLE
            binding.cardDelete.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorDisabled))
            binding.cardClose.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorDisabled))
            binding.editExperiences.requestFocusFromTouch()
        }
        binding.cardSave.setOnClickListener {
            val list = mutableListOf<String>()

            if (binding.cardCutsOn.visibility == View.VISIBLE) {
                list.add("Cortes y peinados")
            }
            if (binding.cardTinctureOn.visibility == View.VISIBLE) {
                list.add("Tintura")
            }
            if (binding.cardTreatmentsOn.visibility == View.VISIBLE) {
                list.add("Tratamientos")
            }
            if (binding.cardManicureOn.visibility == View.VISIBLE) {
                list.add("Manicura")
            }

            val sections = list.toString().replace(","," -").replace("[","").replace("]","")

            if (sections.isNotEmpty() && binding.editName.text.isNotEmpty() && binding.editExperiences.text.isNotEmpty()
                && binding.editHobbies.text.isNotEmpty()) {

                val employees = Employees(
                    experiences = "${binding.editExperiences.text}",
                    hobbies = "${binding.editHobbies.text}",
                    image = "",
                    name = "${binding.editName.text}",
                    sections = sections
                )

                observerUpdateEmployees(employees)

            } else {
                Toast.makeText(context, "Por favor, revise los datos ingresados!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.cardDelete.setOnClickListener {
            observerDeleteEmployees(arguments?.getString("name")!!)
        }
        binding.cardClose.setOnClickListener { dismiss() }
        binding.layoutChange.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)
            }
        }
    }

    private fun observerUpdateEmployees(employees: Employees) {
        viewModel.liveUpdateEmployees(employees).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setTextsDisabled()
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
                    setTextsEnabled()
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE

                    Log.e("FIRESTORE UPDATE", result.exception.toString())

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
            observerUpdateImage(data?.data!!, "${binding.editName.text}")
        }
    }

    private fun observerUpdateImage(image: Uri, imageName: String) {
        viewModel.liveUpdateImage(image, imageName).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE
                    Log.d("PRUEBA", result.data)
                    Glide.with(requireContext()).load(result.data).into(binding.circleImage)
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

    private fun observerDeleteEmployees(employee: String) {
        viewModel.liveDeleteEmployees(employee).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setTextsDisabled()
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
                    setTextsEnabled()
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE DELETE", result.exception.toString())

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