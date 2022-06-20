package com.pelutime.ui.main.admin.home

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
import com.pelutime.ui.main.admin.home.adapters.AdminHomeFutureAdapter
import com.pelutime.ui.main.admin.home.adapters.AdminHomePendingAdapter
import com.pelutime.ui.main.admin.home.adapters.AdminHomeRejectedAdapter
import com.pelutime.ui.main.admin.home.adapters.AdminHomeTodayAdapter
import com.pelutime.application.State
import com.pelutime.data.model.main.home.*
import com.pelutime.databinding.FragmentAdminHomeBinding
import com.pelutime.presentation.main.admin.home.AdminHomeViewModel
import com.pelutime.ui.main.admin.home.dialogs.AdminHomeDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class AdminHomeFragment : Fragment(), AdminHomeTodayAdapter.OnRowClickListener, AdminHomePendingAdapter.OnRowClickListener,
    AdminHomeRejectedAdapter.OnRowClickListener, AdminHomeFutureAdapter.OnRowClickListener {

    // VIEW BINDING
    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<AdminHomeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclers()
        observerGetUserRoom()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRecyclers() {
        binding.recyclerToday.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPending.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRejected.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFuture.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showLoadings() {
        binding.layoutProgressToday.visibility = View.VISIBLE
        binding.layoutProgressPending.visibility = View.VISIBLE
        binding.layoutProgressRejected.visibility = View.VISIBLE
        binding.layoutProgressFuture.visibility = View.VISIBLE
    }

    private fun hideLoadings() {
        binding.layoutProgressToday.visibility = View.GONE
        binding.layoutProgressPending.visibility = View.GONE
        binding.layoutProgressRejected.visibility = View.GONE
        binding.layoutProgressFuture.visibility = View.GONE
    }

    private fun showMessageEmpty(homeAdmin: HomeAdmin) {
        if (homeAdmin.today.isNullOrEmpty()) {
            binding.layoutEmptyToday.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyToday.visibility = View.GONE
        }

        if (homeAdmin.pending.isNullOrEmpty()) {
            binding.layoutEmptyPending.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyPending.visibility = View.GONE
        }

        if (homeAdmin.rejected.isNullOrEmpty()) {
            binding.layoutEmptyRejected.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyRejected.visibility = View.GONE
        }

        if (homeAdmin.future.isNullOrEmpty()) {
            binding.layoutEmptyFuture.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyFuture.visibility = View.GONE
        }
    }

    private fun observerGetUserRoom() {
        viewModel.liveGetUserRoom.observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> { }
                is State.Success -> {
                    for (user in result.data.keys) {
                        binding.textGreeting.text = user
                        break
                    }
                    observerGetDocuments()
                }
                is State.Failure -> {
                    Log.e("ROOM GET USER", result.exception.toString())
                    observerGetDocuments()
                }
            }
        }
    }

    private fun observerGetDocuments() {
        viewModel.liveGetDocuments.observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    if (binding.recyclerToday.adapter == null) {
                        showLoadings()
                    }
                }
                is State.Success -> {
                    hideLoadings()
                    showMessageEmpty(result.data)
                    binding.recyclerToday.adapter = AdminHomeTodayAdapter(requireContext(), result.data.today, this)
                    binding.recyclerPending.adapter = AdminHomePendingAdapter(requireContext(), result.data.pending, this)
                    binding.recyclerRejected.adapter = AdminHomeRejectedAdapter(requireContext(), result.data.rejected, this)
                    binding.recyclerFuture.adapter = AdminHomeFutureAdapter(requireContext(), result.data.future, this)
                }
                is State.Failure -> {
                    hideLoadings()
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

    override fun onItemClickToday(item: HomeToday, position: Int) {
        val bundle = Bundle()

        bundle.putString("id", item.id)
        bundle.putString("image", item.image)
        bundle.putString("nameEmployee", item.nameEmployee)
        bundle.putString("nameUser", item.nameUser)
        bundle.putString("schedule", item.schedule)
        bundle.putString("sectionSelected", item.sectionSelected)
        bundle.putString("textButton", "SOLO RECHAZAR")

        val adminHomeDialogFragment = AdminHomeDialogFragment()
        adminHomeDialogFragment.arguments = bundle

        findNavController().navigate(R.id.adminHomeDialogFragment, bundle)
    }

    override fun onItemClickPending(item: HomePending, position: Int) {
        val bundle = Bundle()

        bundle.putString("id", item.id)
        bundle.putString("image", item.image)
        bundle.putString("nameEmployee", item.nameEmployee)
        bundle.putString("nameUser", item.nameUser)
        bundle.putString("schedule", item.schedule)
        bundle.putString("sectionSelected", item.sectionSelected)
        bundle.putString("textButton", "RECHAZAR")

        val adminHomeDialogFragment = AdminHomeDialogFragment()
        adminHomeDialogFragment.arguments = bundle

        findNavController().navigate(R.id.adminHomeDialogFragment, bundle)
    }

    override fun onItemClickRejected(item: HomeRejected, position: Int) {
        val bundle = Bundle()

        bundle.putString("id", item.id)
        bundle.putString("image", item.image)
        bundle.putString("nameEmployee", item.nameEmployee)
        bundle.putString("nameUser", item.nameUser)
        bundle.putString("schedule", item.schedule)
        bundle.putString("sectionSelected", item.sectionSelected)
        bundle.putString("textButton", "LIBERAR")

        val adminHomeDialogFragment = AdminHomeDialogFragment()
        adminHomeDialogFragment.arguments = bundle

        findNavController().navigate(R.id.adminHomeDialogFragment, bundle)
    }

    override fun onItemClickFuture(item: HomeFuture, position: Int) {
        val bundle = Bundle()

        bundle.putString("id", item.id)
        bundle.putString("image", item.image)
        bundle.putString("nameEmployee", item.nameEmployee)
        bundle.putString("nameUser", item.nameUser)
        bundle.putString("schedule", item.schedule)
        bundle.putString("sectionSelected", item.sectionSelected)
        bundle.putString("textButton", "SOLO RECHAZAR")

        val adminHomeDialogFragment = AdminHomeDialogFragment()
        adminHomeDialogFragment.arguments = bundle

        findNavController().navigate(R.id.adminHomeDialogFragment, bundle)
    }
}