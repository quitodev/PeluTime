package com.pelutime.ui.main.users.home.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pelutime.R
import com.pelutime.core.BaseViewHolder
import com.pelutime.data.model.main.home.HomeFree
import com.pelutime.databinding.RecyclerRowBinding

class UsersHomeFreeAdapter(private val context: Context, private val list: List<HomeFree>,
                           private val onRowClickListener: OnRowClickListener) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnRowClickListener {
        fun onImageClickFree(item: HomeFree, position: Int)
        fun onItemClickFree(item: HomeFree, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = UsersHomeFreeViewHolder(binding)

        binding.circleImage.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onImageClickFree(list[position], position)
        }
        binding.root.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            onRowClickListener.onItemClickFree(list[position], position)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
       when (holder) {
            is UsersHomeFreeViewHolder -> holder.bind(list[position])
       }
    }

    private inner class UsersHomeFreeViewHolder(val binding: RecyclerRowBinding) : BaseViewHolder<HomeFree>(binding.root) {
        override fun bind(item: HomeFree) {
            if (item.image == "Vacío") {
                binding.circleImage.setImageResource(R.drawable.ic_logo)
            } else {
                Glide.with(context).load(item.image).into(binding.circleImage)
            }
            binding.editName.text = item.name
            binding.textSection.text = item.sections
            binding.editSchedule.text = item.minutes
            binding.editSchedule.setTextColor(Color.parseColor("#4CAF50"))
            binding.imageButton.setImageResource(R.drawable.ic_book_now)
        }
    }
}