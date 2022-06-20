package com.pelutime.ui.main.users.booking.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.core.BaseViewHolder
import com.pelutime.data.model.main.booking.BookingEmployees
import com.pelutime.databinding.RecyclerRowBookingEmployeeBinding

class UsersBookingEmployeesAdapter(private val context: Context, private val list: List<BookingEmployees>,
                                   private val onRowClickListener: OnRowClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var selectedPosition = -1

    interface OnRowClickListener {
        fun onImageClickEmployee(item: BookingEmployees, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = RecyclerRowBookingEmployeeBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = UsersBookingEmployeesViewHolder(binding)

        binding.circleImage.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
            onRowClickListener.onImageClickEmployee(list[position], position)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       when (holder) {
            is UsersBookingEmployeesViewHolder -> holder.bind(list[position])
       }
    }

    private inner class UsersBookingEmployeesViewHolder(val binding: RecyclerRowBookingEmployeeBinding) : BaseViewHolder<BookingEmployees>(binding.root) {
        override fun bind(item: BookingEmployees) {
            binding.editName.text = item.name

            if (item.image == "Vacío") {
                binding.circleImage.setImageResource(R.drawable.ic_logo)
            } else {
                Glide.with(context).load(item.image).into(binding.circleImage)
            }

            if (selectedPosition == this.adapterPosition) {
                binding.circleImage.borderColor = Color.parseColor("#FF0057")
                binding.editName.setTextColor(Color.parseColor("#FF0057"))
            } else {
                binding.circleImage.borderColor = Color.parseColor("#A5A5A5")
                binding.editName.setTextColor(Color.parseColor("#A5A5A5"))
            }
        }
    }
}