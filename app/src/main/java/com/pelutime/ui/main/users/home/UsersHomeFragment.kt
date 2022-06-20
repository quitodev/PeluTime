package com.pelutime.ui.main.users.home

import android.content.Intent
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
import com.pelutime.ui.main.users.home.adapters.UsersHomeConfirmedAdapter
import com.pelutime.ui.main.users.home.adapters.UsersHomeFreeAdapter
import com.pelutime.ui.main.users.home.adapters.UsersHomePendingAdapter
import com.pelutime.ui.main.users.home.adapters.UsersHomeRejectedAdapter
import com.pelutime.ui.main.users.home.dialogs.UsersHomeConfirmFragment
import com.pelutime.ui.main.users.home.dialogs.UsersHomeDeleteFragment
import com.pelutime.ui.main.users.home.dialogs.UsersHomeEmployeeFragment
import com.pelutime.application.State
import com.pelutime.application.mapSuccess
import com.pelutime.data.model.main.home.*
import com.pelutime.databinding.FragmentUsersHomeBinding
import com.pelutime.presentation.main.users.home.UsersHomeViewModel
import com.pelutime.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class UsersHomeFragment : Fragment(), UsersHomeConfirmedAdapter.OnRowClickListener, UsersHomeFreeAdapter.OnRowClickListener,
    UsersHomePendingAdapter.OnRowClickListener, UsersHomeRejectedAdapter.OnRowClickListener {

    // VIEW BINDING
    private var _binding: FragmentUsersHomeBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by viewModels<UsersHomeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsersHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclers()
        setBackButton()
        observerGetUserRoom()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRecyclers() {
        binding.recyclerConfirmed.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPending.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRejected.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFree.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setBackButton() {
        binding.cardBack.setOnClickListener {
            viewModel.liveUpdatePlace.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is State.Loading -> {
                        binding.viewEmpty.visibility = View.VISIBLE
                        binding.layoutProgress.visibility = View.VISIBLE
                    }
                    is State.Success -> {
                        Intent(context, LoginActivity::class.java).apply {
                            startActivity(this)
                            activity?.finish()
                        }
                    }
                    is State.Failure -> {
                        binding.viewEmpty.visibility = View.GONE
                        binding.layoutProgress.visibility = View.GONE
                        Log.e("FIRESTORE PLACE", result.exception.toString())

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
                    if (binding.recyclerConfirmed.adapter == null) {
                        showLoadings()
                    }
                }
                is State.Success -> {
                    hideLoadings()
                    showMessageEmpty(result.data)
                    binding.recyclerConfirmed.adapter = UsersHomeConfirmedAdapter(requireContext(), result.data.confirmed, this)
                    binding.recyclerPending.adapter = UsersHomePendingAdapter(requireContext(), result.data.pending, this)
                    binding.recyclerRejected.adapter = UsersHomeRejectedAdapter(requireContext(), result.data.rejected, this)
                    binding.recyclerFree.adapter = UsersHomeFreeAdapter(requireContext(), result.data.free, this)
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

    private fun showLoadings() {
        binding.layoutProgressConfirmed.visibility = View.VISIBLE
        binding.layoutProgressPending.visibility = View.VISIBLE
        binding.layoutProgressRejected.visibility = View.VISIBLE
        binding.layoutProgressFree.visibility = View.VISIBLE
    }

    private fun hideLoadings() {
        binding.layoutProgressConfirmed.visibility = View.GONE
        binding.layoutProgressPending.visibility = View.GONE
        binding.layoutProgressRejected.visibility = View.GONE
        binding.layoutProgressFree.visibility = View.GONE
    }

    private fun showMessageEmpty(homeUsers: HomeUsers) {
        if (homeUsers.confirmed.isNullOrEmpty()) {
            binding.layoutEmptyConfirmed.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyConfirmed.visibility = View.GONE
        }

        if (homeUsers.pending.isNullOrEmpty()) {
            binding.layoutEmptyPending.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyPending.visibility = View.GONE
        }

        if (homeUsers.rejected.isNullOrEmpty()) {
            binding.layoutEmptyRejected.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyRejected.visibility = View.GONE
        }

        if (homeUsers.free.isNullOrEmpty()) {
            binding.layoutEmptyFree.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyFree.visibility = View.GONE
        }
    }

    override fun onImageClickConfirmed(item: HomeConfirmed, position: Int) {
        val bundle = Bundle()

        bundle.putString("experiences", item.experiences)
        bundle.putString("hobbies", item.hobbies)
        bundle.putString("image", item.image)
        bundle.putString("name", item.name)
        bundle.putString("sections", item.sections)

        val usersHomeEmployeeFragment = UsersHomeEmployeeFragment()
        usersHomeEmployeeFragment.arguments = bundle

        findNavController().navigate(R.id.usersHomeEmployeeFragment, bundle)
    }

    override fun onItemClickConfirmed(item: HomeConfirmed, position: Int) {
        val bundle = Bundle()

        bundle.putString("image", item.image)
        bundle.putString("name", item.name)
        bundle.putString("schedule", item.schedule)
        bundle.putString("sectionSelected", item.sectionSelected)
        bundle.putString("status", "RESERVADO")

        val usersHomeDeleteFragment = UsersHomeDeleteFragment()
        usersHomeDeleteFragment.arguments = bundle

        findNavController().navigate(R.id.usersHomeDeleteFragment, bundle)
    }

    override fun onImageClickPending(item: HomePending, position: Int) {
        val bundle = Bundle()

        bundle.putString("experiences", item.experiences)
        bundle.putString("hobbies", item.hobbies)
        bundle.putString("image", item.image)
        bundle.putString("name", item.nameEmployee)
        bundle.putString("sections", item.sections)

        val usersHomeEmployeeFragment = UsersHomeEmployeeFragment()
        usersHomeEmployeeFragment.arguments = bundle

        findNavController().navigate(R.id.usersHomeEmployeeFragment, bundle)
    }

    override fun onItemClickPending(item: HomePending, position: Int) {
        val bundle = Bundle()

        bundle.putString("image", item.image)
        bundle.putString("name", item.nameEmployee)
        bundle.putString("schedule", item.schedule)
        bundle.putString("sectionSelected", item.sectionSelected)
        bundle.putString("status", "PENDIENTE")

        val usersHomeDeleteFragment = UsersHomeDeleteFragment()
        usersHomeDeleteFragment.arguments = bundle

        findNavController().navigate(R.id.usersHomeDeleteFragment, bundle)
    }

    override fun onImageClickRejected(item: HomeRejected, position: Int) {
        val bundle = Bundle()

        bundle.putString("experiences", item.experiences)
        bundle.putString("hobbies", item.hobbies)
        bundle.putString("image", item.image)
        bundle.putString("name", item.nameEmployee)
        bundle.putString("sections", item.sections)

        val usersHomeEmployeeFragment = UsersHomeEmployeeFragment()
        usersHomeEmployeeFragment.arguments = bundle

        findNavController().navigate(R.id.usersHomeEmployeeFragment, bundle)
    }

    override fun onImageClickFree(item: HomeFree, position: Int) {
        val bundle = Bundle()

        bundle.putString("experiences", item.experiences)
        bundle.putString("hobbies", item.hobbies)
        bundle.putString("image", item.image)
        bundle.putString("name", item.name)
        bundle.putString("sections", item.sections)

        val usersHomeEmployeeFragment = UsersHomeEmployeeFragment()
        usersHomeEmployeeFragment.arguments = bundle

        findNavController().navigate(R.id.usersHomeEmployeeFragment, bundle)
    }

    override fun onItemClickFree(item: HomeFree, position: Int) {
        val bundle = Bundle()

        viewModel.liveGetUserRoom.value!!.mapSuccess { dataUser ->
            for (user in dataUser.keys) {
                bundle.putString("id", dataUser[user]?.id)
                bundle.putString("image", item.image)
                bundle.putString("nameEmployee", item.name)
                bundle.putString("nameUser", dataUser[user]?.name)
                bundle.putString("schedule", item.schedule)
                bundle.putString("sections", item.sections)
                break
            }
        }

        val usersHomeConfirmFragment = UsersHomeConfirmFragment()
        usersHomeConfirmFragment.arguments = bundle

        findNavController().navigate(R.id.usersHomeConfirmFragment, bundle)
    }
}