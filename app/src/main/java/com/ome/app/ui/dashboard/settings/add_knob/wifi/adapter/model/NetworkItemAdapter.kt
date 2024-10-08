package com.ome.app.ui.dashboard.settings.add_knob.wifi.adapter.model

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ome.Ome.R
import com.ome.Ome.databinding.NetworkItemBinding
import com.ome.app.ui.base.recycler.AdapterDelegate
import com.ome.app.ui.base.recycler.ItemModel


class NetworkItemAdapter(
    context: Context,
    private val itemListener: (NetworkItemModel) -> Unit
) : AdapterDelegate(context) {

    override fun onCreateViewHolder(parent: ViewGroup): NetworkViewHolder =
        NetworkViewHolder(
            NetworkItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        item: ItemModel,
        position: Int,
        itemCount: Int
    ) {
        (viewHolder as NetworkViewHolder).bind(item as NetworkItemModel, position, itemCount)
    }

    override fun isForViewType(item: ItemModel, position: Int): Boolean =
        item is NetworkItemModel

    override fun getViewType(): Int = R.layout.network_item

    inner class NetworkViewHolder(
        private val binding: NetworkItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NetworkItemModel, position: Int, itemsCount: Int) {
            binding.parent.setOnClickListener {
                itemListener.invoke(item)
            }
            binding.networkTv.text = item.ssid

        }
    }
}