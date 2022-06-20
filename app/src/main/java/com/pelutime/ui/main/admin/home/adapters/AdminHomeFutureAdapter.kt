package com.pelutime.ui.main.admin.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.core.BaseViewHolder
import com.pelutime.data.model.main.home.HomeFuture
import com.pelutime.databinding.RecyclerRowBinding

class AdminHomeFutureAdapter(private val context: Context, private val list: List<HomeFuture>,
                             private val onRowClickListener: OnRowClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRowClickListener {
        fun onItemClickFuture(item: HomeFuture, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = AdminHomeFutureViewHolder(binding)

        binding.root.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onItemClickFuture(list[position], position)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       when (holder) {
            is AdminHomeFutureViewHolder -> holder.bind(list[position])
       }
    }

    private inner class AdminHomeFutureViewHolder(val binding: RecyclerRowBinding) : BaseViewHolder<HomeFuture>(binding.root) {
        override fun bind(item: HomeFuture) {
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