package com.pelutime.ui.main.admin.employees.dialogs

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
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.application.State
import com.pelutime.databinding.FragmentAdminEmployeesNewDialogBinding
import com.pelutime.presentation.main.admin.employees.AdminEmployeesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AdminEmployeesNewFragment : DialogFragment() {

    // VIEW BINDING
    private var _binding: FragmentAdminEmployeesNewDialogBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<AdminEmployeesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentAdminEmployeesNewDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSectionsButtons()
        setEventsButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setTextsEnabled() {
        binding.editName.isEnabled = true
        binding.editExperiences.isEnabled = true
        binding.editHobbies.isEnabled = true
        binding.editName.isFocusableInTouchMode = true
        binding.editExperiences.isFocusableInTouchMode = true
        binding.editHobbies.isFocusableInTouchMode = true
    }

    private fun setTextsDisabled() {
        binding.editName.isEnabled = false
        binding.editExperiences.isEnabled = false
        binding.editHobbies.isEnabled = false
        binding.editName.isFocusableInTouchMode = false
        binding.editExperiences.isFocusableInTouchMode = false
        binding.editHobbies.isFocusableInTouchMode = false
        binding.editName.isFocusable = false
        binding.editExperiences.isFocusable = false
        binding.editHobbies.isFocusable = false
    }

    private fun setViewsEnabled() {
        binding.cardConfirm.isEnabled = true
        binding.cardClose.isEnabled = true
    }

    private fun setViewsDisabled() {
        binding.cardConfirm.isEnabled = false
        binding.cardClose.isEnabled = false
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
        val list = mutableListOf<String>()

        binding.cardConfirm.setOnClickListener {
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

            if (binding.editName.text.isNotEmpty() && sections.isNotEmpty() && binding.editExperiences.text.isNotEmpty()
                && binding.editHobbies.text.isNotEmpty()) {

                val employees = Employees(
                    experiences = "${binding.editExperiences.text}",
                    hobbies = "${binding.editHobbies.text}",
                    image = "Vacío",
                    name = "${binding.editName.text}",
                    sections = sections
                )

                observerSetEmployees(employees)

            } else {
                Toast.makeText(context,
                    "Por favor, revise los datos ingresados! Tenga en cuenta que si hay otro empleado/a con el mismo nombre, puede agregarle " +
                            "alguna inicial o algún sobrenombre", Toast.LENGTH_LONG).show()
            }
        }

        binding.cardClose.setOnClickListener { dismiss() }
    }

    private fun observerSetEmployees(employees: Employees) {
        viewModel.liveSetEmployees(employees).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setTextsDisabled()
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    when (result.data) {
                        "Documentos creados con éxito" -> {
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
                        "Nombre en uso" -> {
                            if (binding.layoutProgress.visibility == View.VISIBLE) {
                                dismiss()
                            }
                        }
                    }
                    Toast.makeText(context,
                        "Tenga en cuenta que si hay otro empleado/a con el mismo nombre, debe agregarle " +
                                "alguna inicial o algún sobrenombre", Toast.LENGTH_LONG).show()
                }
                is State.Failure -> {
                    setTextsEnabled()
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE EMPLOYEES", result.exception.toString())

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