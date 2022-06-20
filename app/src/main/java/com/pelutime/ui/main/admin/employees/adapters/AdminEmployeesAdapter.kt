package com.pelutime.ui.main.admin.employees.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.core.BaseViewHolder
import com.pelutime.data.model.main.employees.Employees
import com.pelutime.databinding.RecyclerRowBinding

class AdminEmployeesAdapter(private val context: Context, private val list: List<Employees>,
                            private val onRowClickListener: OnRowClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRowClickListener {
        fun onItemClickEmployee(item: Employees, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = AdminEmployeesViewHolder(binding)

        binding.root.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onItemClickEmployee(list[position], position)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       when (holder) {
            is AdminEmployeesViewHolder -> holder.bind(list[position])
       }
    }

    private inner class AdminEmployeesViewHolder(val binding: RecyclerRowBinding) : BaseViewHolder<Employees>(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun bind(item: Employees) {
            if (item.image == "Vacío") {
                binding.circleImage.setImageResource(R.drawable.ic_logo)
            } else {
                Glide.with(context).load(item.image).into(binding.circleImage)
            }
            binding.editName.text = item.name
            binding.textSection.text = item.sections
            binding.editSchedule.text = "ACTIVO"
            binding.editSchedule.setTextColor(Color.parseColor("#4CAF50"))
            binding.imageButton.setImageResource(R.drawable.ic_change)
        }
    }
}