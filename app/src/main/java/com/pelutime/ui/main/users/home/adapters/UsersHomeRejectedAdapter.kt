package com.pelutime.ui.main.users.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.core.BaseViewHolder
import com.pelutime.data.model.main.home.HomeRejected
import com.pelutime.databinding.RecyclerRowBinding

class UsersHomeRejectedAdapter(private val context: Context, private val list: List<HomeRejected>,
                               private val onRowClickListener: OnRowClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRowClickListener {
        fun onImageClickRejected(item: HomeRejected, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = UsersHomeRejectedViewHolder(binding)

        binding.circleImage.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onImageClickRejected(list[position], position)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       when (holder) {
            is UsersHomeRejectedViewHolder -> holder.bind(list[position])
       }
    }

    private inner class UsersHomeRejectedViewHolder(val binding: RecyclerRowBinding) : BaseViewHolder<HomeRejected>(binding.root) {
        override fun bind(item: HomeRejected) {
            if (item.image == "Vacío") {
                binding.circleImage.setImageResource(R.drawable.ic_logo)
            } else {
                Glide.with(context).load(item.image).into(binding.circleImage)
            }
            binding.editName.text = item.nameEmployee
            binding.textSection.text = item.sectionSelected
            binding.editSchedule.text = item.schedule
        }
    }
}