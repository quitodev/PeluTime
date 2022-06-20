package com.pelutime.ui.main.admin.employees

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
import com.pelutime.R
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.ui.main.admin.employees.adapters.AdminEmployeesAdapter
import com.pelutime.ui.main.admin.employees.dialogs.AdminEmployeesUpdateFragment
import com.pelutime.application.State
import com.pelutime.databinding.FragmentAdminEmployeesBinding
import com.pelutime.presentation.main.admin.employees.AdminEmployeesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AdminEmployeesFragment : Fragment(), AdminEmployeesAdapter.OnRowClickListener {

    // VIEW BINDING
    private var _binding: FragmentAdminEmployeesBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<AdminEmployeesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminEmployeesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerEmployees.layoutManager = LinearLayoutManager(requireContext())

        setEventsButtons()
        observerGetEmployees()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEventsButtons() {
        binding.cardNew.setOnClickListener {
            findNavController().navigate(R.id.adminEmployeesNewFragment)
        }
    }

    private fun observerGetEmployees() {
        viewModel.liveGetEmployees.observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    if (binding.recyclerEmployees.adapter == null) {
                        binding.layoutProgressEmployees.visibility = View.VISIBLE
                    }
                }
                is State.Success -> {
                    binding.layoutProgressEmployees.visibility = View.GONE
                    binding.recyclerEmployees.adapter = AdminEmployeesAdapter(requireContext(), result.data, this)
                }
                is State.Failure -> {
                    binding.layoutProgressEmployees.visibility = View.GONE
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

    override fun onItemClickEmployee(item: Employees, position: Int) {
        val bundle = Bundle()

        bundle.putString("experiences", item.experiences)
        bundle.putString("hobbies", item.hobbies)
        bundle.putString("image", item.image)
        bundle.putString("name", item.name)
        bundle.putString("sections", item.sections)

        val adminEmployeesUpdateFragment = AdminEmployeesUpdateFragment()
        adminEmployeesUpdateFragment.arguments = bundle

        findNavController().navigate(R.id.adminEmployeesUpdateFragment, bundle)
    }
}