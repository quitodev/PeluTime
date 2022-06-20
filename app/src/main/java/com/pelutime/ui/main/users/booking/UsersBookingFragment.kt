package com.pelutime.ui.main.users.booking

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pelutime.R
import com.pelutime.data.model.main.booking.BookingEmployees
import com.pelutime.data.model.main.booking.BookingSchedule
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.ui.main.users.booking.adapters.UsersBookingEmployeesAdapter
import com.pelutime.ui.main.users.booking.adapters.UsersBookingScheduleAdapter
import com.pelutime.ui.main.users.booking.dialogs.UsersBookingConfirmFragment
import com.pelutime.ui.main.users.booking.dialogs.UsersBookingEmployeeFragment
import com.pelutime.application.State
import com.pelutime.databinding.FragmentUsersBookingBinding
import com.pelutime.presentation.main.users.booking.UsersBookingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class UsersBookingFragment : Fragment(), UsersBookingEmployeesAdapter.OnRowClickListener, UsersBookingScheduleAdapter.OnRowClickListener {

    // VIEW BINDING
    private var _binding: FragmentUsersBookingBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<UsersBookingViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsersBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclers()
        setSectionsButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRecyclers() {
        binding.recyclerEmployee.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.recyclerSchedule.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setSectionsButtons() {
        binding.cardCuts.setOnClickListener {
            binding.layoutSchedule.visibility = View.GONE

            observerGetEmployees("Cortes y peinados")

            binding.imageCuts.setImageResource(R.drawable.ic_cortes_back_light)
            binding.imageTincture.setImageResource(R.drawable.ic_tintura_back_dark)
            binding.imageTreatments.setImageResource(R.drawable.ic_tratamientos_back_dark)
            binding.imageManicure.setImageResource(R.drawable.ic_manicura_back_dark)

            binding.imageCuts.tag = R.drawable.ic_cortes_back_light
            binding.imageTincture.tag = R.drawable.ic_tintura_back_dark
            binding.imageTreatments.tag = R.drawable.ic_tratamientos_back_dark
            binding.imageManicure.tag = R.drawable.ic_manicura_back_dark
        }
        binding.cardTincture.setOnClickListener {
            binding.layoutSchedule.visibility = View.GONE

            observerGetEmployees("Tintura")

            binding.imageCuts.setImageResource(R.drawable.ic_cortes_back_dark)
            binding.imageTincture.setImageResource(R.drawable.ic_tintura_back_light)
            binding.imageTreatments.setImageResource(R.drawable.ic_tratamientos_back_dark)
            binding.imageManicure.setImageResource(R.drawable.ic_manicura_back_dark)

            binding.imageCuts.tag = R.drawable.ic_cortes_back_dark
            binding.imageTincture.tag = R.drawable.ic_tintura_back_light
            binding.imageTreatments.tag = R.drawable.ic_tratamientos_back_dark
            binding.imageManicure.tag = R.drawable.ic_manicura_back_dark
        }
        binding.cardTreatments.setOnClickListener {
            binding.layoutSchedule.visibility = View.GONE

            observerGetEmployees("Tratamientos")

            binding.imageCuts.setImageResource(R.drawable.ic_cortes_back_dark)
            binding.imageTincture.setImageResource(R.drawable.ic_tintura_back_dark)
            binding.imageTreatments.setImageResource(R.drawable.ic_tratamientos_back_light)
            binding.imageManicure.setImageResource(R.drawable.ic_manicura_back_dark)

            binding.imageCuts.tag = R.drawable.ic_cortes_back_dark
            binding.imageTincture.tag = R.drawable.ic_tintura_back_dark
            binding.imageTreatments.tag = R.drawable.ic_tratamientos_back_light
            binding.imageManicure.tag = R.drawable.ic_manicura_back_dark
        }
        binding.cardManicure.setOnClickListener {
            binding.layoutSchedule.visibility = View.GONE

            observerGetEmployees("Manicura")

            binding.imageCuts.setImageResource(R.drawable.ic_cortes_back_dark)
            binding.imageTincture.setImageResource(R.drawable.ic_tintura_back_dark)
            binding.imageTreatments.setImageResource(R.drawable.ic_tratamientos_back_dark)
            binding.imageManicure.setImageResource(R.drawable.ic_manicura_back_light)

            binding.imageCuts.tag = R.drawable.ic_cortes_back_dark
            binding.imageTincture.tag = R.drawable.ic_tintura_back_dark
            binding.imageTreatments.tag = R.drawable.ic_tratamientos_back_dark
            binding.imageManicure.tag = R.drawable.ic_manicura_back_light
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observerGetEmployees(section: String) {
        viewModel.liveGetEmployees(section).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    binding.layoutProgress.visibility = View.GONE
                    binding.layoutEmployees.visibility = View.VISIBLE

                    if (result.data.isNullOrEmpty()) {
                        binding.layoutEmptyEmployees.visibility = View.VISIBLE
                        binding.textEmptyEmployees.text = "Por el momento no tenemos personal haciendo ${section}..."
                    } else {
                        binding.layoutEmptyEmployees.visibility = View.GONE
                    }

                    binding.recyclerEmployee.adapter = UsersBookingEmployeesAdapter(requireContext(), result.data, this)

                    Timer().schedule(200){
                        binding.scrollView.smoothScrollTo(0,5000)
                    }
                }
                is State.Failure -> {
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

    override fun onImageClickEmployee(item: BookingEmployees, position: Int) {
        val bundle = Bundle()

        bundle.putString("experiences", item.experiences)
        bundle.putString("hobbies", item.hobbies)
        bundle.putString("image", item.image)
        bundle.putString("name", item.name)
        bundle.putString("sections", item.sections)

        val usersBookingEmployeeFragment = UsersBookingEmployeeFragment()
        usersBookingEmployeeFragment.arguments = bundle

        findNavController().navigate(R.id.usersBookingEmployeeFragment, bundle)

        val listEmployees = listOf(Employees(item.experiences, item.hobbies, item.image, item.name, item.sections))
        observerGetDocuments(listEmployees, item)
    }

    @SuppressLint("SetTextI18n")
    private fun observerGetDocuments(listEmployees: List<Employees>, bookingEmployees: BookingEmployees) {
        viewModel.liveGetDocuments(listEmployees, bookingEmployees).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    binding.layoutProgress.visibility = View.GONE
                    binding.layoutSchedule.visibility = View.VISIBLE

                    if (result.data.isNullOrEmpty()) {
                        binding.layoutEmptySchedule.visibility = View.VISIBLE
                        binding.textEmptyEmployees.text = "${bookingEmployees.name} no estará atendiendo los próximos días..."
                    } else {
                        binding.layoutEmptySchedule.visibility = View.GONE
                    }

                    binding.recyclerSchedule.adapter = UsersBookingScheduleAdapter(requireContext(), result.data, this)

                    Timer().schedule(200){
                        binding.scrollView.smoothScrollTo(0,5000)
                    }
                }
                is State.Failure -> {
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE DOCUMENTS", result.exception.toString())

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

    override fun onItemClickFree(item: BookingSchedule, position: Int) {
        val bundle = Bundle()

        bundle.putString("name", item.name)
        bundle.putString("image", item.image)
        bundle.putString("schedule", item.schedule)

        when {
            binding.imageCuts.tag == R.drawable.ic_cortes_back_light -> {
                bundle.putString("section", "Cortes y peinados")
            }
            binding.imageTincture.tag == R.drawable.ic_tintura_back_light -> {
                bundle.putString("section", "Tintura")
            }
            binding.imageTreatments.tag == R.drawable.ic_tratamientos_back_light -> {
                bundle.putString("section", "Tratamientos")
            }
            binding.imageManicure.tag == R.drawable.ic_manicura_back_light -> {
                bundle.putString("section", "Manicura")
            }
        }

        val usersBookingConfirmFragment = UsersBookingConfirmFragment()
        usersBookingConfirmFragment.arguments = bundle

        findNavController().navigate(R.id.usersBookingConfirmFragment, bundle)
    }
}