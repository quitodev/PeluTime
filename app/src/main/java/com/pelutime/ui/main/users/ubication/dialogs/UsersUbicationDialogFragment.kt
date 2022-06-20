package com.pelutime.ui.main.users.ubication.dialogs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.pelutime.databinding.FragmentUsersUbicationDialogBinding

class UsersUbicationDialogFragment : DialogFragment() {

    // VIEW BINDING
    private var _binding: FragmentUsersUbicationDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentUsersUbicationDialogBinding.inflate(inflater, container, false)
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
        binding.cardClose.setOnClickListener { dismiss() }
    }
}