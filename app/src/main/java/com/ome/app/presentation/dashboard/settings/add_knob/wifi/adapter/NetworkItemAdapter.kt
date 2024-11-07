package com.ome.app.presentation.dashboard.settings.add_knob.wifi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ome.app.databinding.NetworkItemBinding
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.adapter.model.NetworkItemModel


class NetworkItemAdapter(private val onClick: (NetworkItemModel) -> Unit) :
    ListAdapter<NetworkItemModel, NetworkItemAdapter.ViewHolder>(COMPARATOR) {

    inner class ViewHolder(
        private val binding: NetworkItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: NetworkItemModel) {
            binding.root.setOnClickListener {
                onClick(item)
            }
            binding.networkTv.text = item.ssid
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(NetworkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<NetworkItemModel>() {
            override fun areItemsTheSame(oldItem: NetworkItemModel, newItem: NetworkItemModel): Boolean = oldItem == newItem
            override fun areContentsTheSame(oldItem: NetworkItemModel, newItem: NetworkItemModel): Boolean = false
        }
    }

}