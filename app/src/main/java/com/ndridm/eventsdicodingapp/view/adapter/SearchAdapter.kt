package com.ndridm.eventsdicodingapp.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ndridm.eventsdicodingapp.data.response.ListEventsItem
import com.ndridm.eventsdicodingapp.databinding.ListItemBinding

class SearchAdapter(private val onClickEvent: (Int) -> Unit
): ListAdapter<ListEventsItem, SearchAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem, onClickEvent: (Int) -> Unit) {
            binding.root.setOnClickListener {
                event.id?.let { itId -> onClickEvent(itId) }
            }

            val ivImageLogo = binding.ivImageLogo
            Glide.with(ivImageLogo).load(event.imageLogo).into(ivImageLogo)
            binding.tvEventTitle.text = event.name
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean = oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: ListEventsItem,
                newItem: ListEventsItem
            ): Boolean = oldItem == newItem

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val geItem = getItem(position)
        if (geItem != null) {
            holder.bind(geItem, onClickEvent)
        }
    }
}