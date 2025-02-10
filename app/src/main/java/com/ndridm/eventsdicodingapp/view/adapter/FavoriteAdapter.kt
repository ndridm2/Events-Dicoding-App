package com.ndridm.eventsdicodingapp.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ndridm.eventsdicodingapp.data.local.entity.EventEntity
import com.ndridm.eventsdicodingapp.databinding.FavoriteItemBinding

class FavoriteAdapter(private val onClickEvent: (EventEntity) -> Unit) :
    ListAdapter<EventEntity, FavoriteAdapter.FavoriteViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: ((EventEntity) -> Unit)? = null
    fun setOnItemClickCallback(callback: (EventEntity) -> Unit) {
        onItemClickCallback = callback
    }

    inner class FavoriteViewHolder(private val binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("CheckResult")
            fun bind(event: EventEntity) {
                with(binding){
                    tvName.text = event.name
                    tvTime.text = event.beginTime

                    Glide.with(itemView.context)
                        .load(event.mediaCover)
                        .apply {
                            placeholder(android.R.color.darker_gray)
                            error(android.R.drawable.stat_notify_error)
                        }
                        .into(imageView)

                    itemView.setOnClickListener{
                        onClickEvent(event)
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

}