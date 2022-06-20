package com.pelutime.ui.main.users.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.core.BaseViewHolder
import com.pelutime.data.model.main.home.HomePending
import com.pelutime.databinding.RecyclerRowBinding

class UsersHomePendingAdapter(private val context: Context, private val list: List<HomePending>,
                              private val onRowClickListener: OnRowClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRowClickListener {
        fun onImageClickPending(item: HomePending, position: Int)
        fun onItemClickPending(item: HomePending, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = UsersHomePendingViewHolder(binding)

        binding.circleImage.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onImageClickPending(list[position], position)
        }
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
            is UsersHomePendingViewHolder -> holder.bind(list[position])
       }
    }

    private inner class UsersHomePendingViewHolder(val binding: RecyclerRowBinding) : BaseViewHolder<HomePending>(binding.root) {
        override fun bind(item: HomePending) {
            if (item.image == "Vacío") {
                binding.circleImage.setImageResource(R.drawable.ic_logo)
            } else {
                Glide.with(context).load(item.image).into(binding.circleImage)
            }
            binding.editName.text = item.nameEmployee
            binding.textSection.text = item.sectionSelected
            binding.editSchedule.text = item.schedule
            binding.imageButton.setImageResource(R.drawable.ic_delete)
        }
    }
}