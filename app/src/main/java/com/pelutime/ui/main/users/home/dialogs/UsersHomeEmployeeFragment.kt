package com.pelutime.ui.main.users.home.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.databinding.FragmentUsersHomeEmployeeDialogBinding

class UsersHomeEmployeeFragment : DialogFragment() {

    // VIEW BINDING
    private var _binding: FragmentUsersHomeEmployeeDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentUsersHomeEmployeeDialogBinding.inflate(inflater, container, false)
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

    private fun setUserData() {
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

        binding.editName.text = arguments?.getString("name")
        binding.textExperiences.text = arguments?.getString("experiences")
        binding.textHobbies.text = arguments?.getString("hobbies")
    }

    private fun setEventsButtons() {
        binding.cardClose.setOnClickListener { dismiss() }
    }
}