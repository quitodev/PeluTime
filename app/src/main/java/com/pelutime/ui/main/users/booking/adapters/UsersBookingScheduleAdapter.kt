package com.pelutime.ui.main.users.booking.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pelutime.core.BaseViewHolder
import com.pelutime.data.model.main.booking.BookingSchedule
import com.pelutime.databinding.RecyclerRowBookingScheduleBinding

class UsersBookingScheduleAdapter(private val context: Context, private val list: List<BookingSchedule>,
                                  private val onRowClickListener: OnRowClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRowClickListener {
        fun onItemClickFree(item: BookingSchedule, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = RecyclerRowBookingScheduleBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = UsersBookingScheduleViewHolder(binding)

        binding.root.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onItemClickFree(list[position], position)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return 200
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       when (holder) {
            is UsersBookingScheduleViewHolder -> holder.bind(list[position])
       }
    }

    private inner class UsersBookingScheduleViewHolder(val binding: RecyclerRowBookingScheduleBinding) : BaseViewHolder<BookingSchedule>(binding.root) {
        @SuppressLint("SetTextI18n")
        override fun bind(item: BookingSchedule) {
            binding.editSchedule.text = item.schedule
            binding.textStatus.text = "LIBRE"
        }
    }
}