package com.pelutime.ui.main.maps.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.pelutime.application.State
import com.pelutime.databinding.FragmentMapsDialogBinding
import com.pelutime.presentation.main.maps.MapsViewModel
import com.pelutime.ui.main.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MapsDialogFragment : DialogFragment() {

    // VIEW BINDING
    private var _binding: FragmentMapsDialogBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by activityViewModels<MapsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentMapsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUbicationData()
        setEventsButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setViewsEnabled() {
        binding.imageUbication.isEnabled = true
        binding.textAddress.isEnabled = true
        binding.textPhone.isEnabled = true
        binding.textWebsite.isEnabled = true
        binding.cardConfirm.isEnabled = true
        binding.cardClose.isEnabled = true
    }

    private fun setViewsDisabled() {
        binding.imageUbication.isEnabled = false
        binding.textAddress.isEnabled = false
        binding.textPhone.isEnabled = false
        binding.textWebsite.isEnabled = false
        binding.cardConfirm.isEnabled = false
        binding.cardClose.isEnabled = false
    }

    private fun setUbicationData() {
        binding.cardImage.visibility = View.VISIBLE

        binding.textName.text = arguments?.getString("name")
        binding.textAddress.text = arguments?.getString("address")
        binding.textSchedule.text = arguments?.getString("schedule")
        binding.textPhone.text = arguments?.getString("phone")
        binding.textWebsite.text = arguments?.getString("website")
        Glide.with(requireContext()).load(arguments?.getString("image")).into(binding.imageUbication)
    }

    private fun setEventsButtons() {
        binding.imageUbication.setOnClickListener {
            binding.viewEmpty.visibility = View.VISIBLE
            binding.layoutImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(arguments?.getString("image")).into(binding.photoView)
        }
        binding.imageClose.setOnClickListener {
            binding.viewEmpty.visibility = View.GONE
            binding.layoutImage.visibility = View.GONE
        }
        binding.textPhone.setOnClickListener {
            Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${arguments?.getString("phone")}")
                startActivity(this)
            }
        }
        binding.textWebsite.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://${arguments?.getString("website")!!.replace("https://", "")}")
                startActivity(this)
            }
        }
        binding.textAddress.setOnClickListener { dismiss() }
        binding.cardConfirm.setOnClickListener {
            observerSetPlace()
        }
        binding.cardClose.setOnClickListener { dismiss() }
    }

    private fun observerSetPlace() {
        viewModel.liveSetPlace(arguments?.getString("place")!!).observe(viewLifecycleOwner) { result ->
            when (result) {
                is State.Loading -> {
                    setViewsDisabled()
                    binding.layoutProgress.visibility = View.VISIBLE
                }
                is State.Success -> {
                    startMain(result.data)
                }
                is State.Failure -> {
                    setViewsEnabled()
                    binding.layoutProgress.visibility = View.GONE
                    Log.e("FIRESTORE PLACES", result.exception.toString())

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

    private fun startMain(userId: String) {
        Intent(context, MainActivity::class.java).apply {
            putExtra("userId", userId)
            putExtra("userPlace", arguments?.getString("place"))
            startActivity(this)
            activity?.finish()
        }
    }
}