package com.pelutime.ui.main.admin.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.core.BaseViewHolder
import com.pelutime.data.model.main.home.HomePending
import com.pelutime.databinding.RecyclerRowBinding

class AdminHomePendingAdapter(private val context: Context, private val list: List<HomePending>,
                              private val onRowClickListener: OnRowClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRowClickListener {
        fun onItemClickPending(item: HomePending, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = AdminHomePendingViewHolder(binding)

        binding.root.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onItemClickPending(list[position], position)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       when (holder) {
            is AdminHomePendingViewHolder -> holder.bind(list[position])
       }
    }

    inner class AdminHomePendingViewHolder(val binding: RecyclerRowBinding) : BaseViewHolder<HomePending>(binding.root) {
        override fun bind(item: HomePending) {
            if (item.image == "Vacío") {
                binding.circleImage.setImageResource(R.drawable.ic_logo)
            } else {
                Glide.with(context).load(item.image).into(binding.circleImage)
            }
            binding.editName.text = item.nameUser
            binding.editSchedule.text = item.schedule
            binding.textSection.text = item.sectionSelected
            binding.imageButton.setImageResource(R.drawable.ic_check)
        }
    }
}