package com.pelutime.ui.main.admin.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.core.BaseViewHolder
import com.pelutime.data.model.main.home.HomeToday
import com.pelutime.databinding.RecyclerRowBinding

class AdminHomeTodayAdapter(private val context: Context, private val list: List<HomeToday>,
                            private val onRowClickListener: OnRowClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRowClickListener {
        fun onItemClickToday(item: HomeToday, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = AdminHomeTodayViewHolder(binding)

        binding.root.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onItemClickToday(list[position], position)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       when (holder) {
            is AdminHomeTodayViewHolder -> holder.bind(list[position])
       }
    }

    private inner class AdminHomeTodayViewHolder(val binding: RecyclerRowBinding) : BaseViewHolder<HomeToday>(binding.root) {
        override fun bind(item: HomeToday) {
            if (item.image == "Vacío") {
                binding.circleImage.setImageResource(R.drawable.ic_logo)
            } else {
                Glide.with(context).load(item.image).into(binding.circleImage)
            }
            binding.editName.text = item.nameUser
            binding.editSchedule.text = item.schedule
            binding.textSection.text = item.sectionSelected
            binding.imageButton.setImageResource(R.drawable.ic_delete)
        }
    }
}