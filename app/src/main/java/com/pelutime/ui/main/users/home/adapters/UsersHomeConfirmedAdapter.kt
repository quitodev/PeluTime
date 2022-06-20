package com.pelutime.ui.main.users.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.core.BaseViewHolder
import com.pelutime.data.model.main.home.HomeConfirmed
import com.pelutime.databinding.RecyclerRowBinding

class UsersHomeConfirmedAdapter(private val context: Context, private val list: List<HomeConfirmed>,
                                private val onRowClickListener: OnRowClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRowClickListener {
        fun onImageClickConfirmed(item: HomeConfirmed, position: Int)
        fun onItemClickConfirmed(item: HomeConfirmed, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = UsersHomeConfirmedViewHolder(binding)

        binding.circleImage.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onImageClickConfirmed(list[position], position)
        }
        binding.root.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onItemClickConfirmed(list[position], position)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       when (holder) {
            is UsersHomeConfirmedViewHolder -> holder.bind(list[position])
       }
    }

    private inner class UsersHomeConfirmedViewHolder(val binding: RecyclerRowBinding) : BaseViewHolder<HomeConfirmed>(binding.root) {
        override fun bind(item: HomeConfirmed) {
            if (item.image == "Vacío") {
                binding.circleImage.setImageResource(R.drawable.ic_logo)
            } else {
                Glide.with(context).load(item.image).into(binding.circleImage)
            }
            binding.editName.text = item.name
            binding.textSection.text = item.sectionSelected
            binding.editSchedule.text = item.schedule
            binding.imageButton.setImageResource(R.drawable.ic_delete)
        }
    }
}