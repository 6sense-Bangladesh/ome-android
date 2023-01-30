package com.ome.app.ui.dashboard.settings.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ome.Ome.R
import com.ome.Ome.databinding.SettingsItemBinding
import com.ome.app.ui.base.recycler.AdapterDelegate
import com.ome.app.ui.base.recycler.ItemModel
import com.ome.app.ui.dashboard.profile.adapter.MessageItemModel


class SettingsItemAdapter(
    context: Context,
    private val itemListener: (SettingsItemModel) -> Unit
) : AdapterDelegate(context) {

    override fun onCreateViewHolder(parent: ViewGroup): SettingViewHolder =
        SettingViewHolder(
            SettingsItemBinding.inflate(
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
        val currentItem = item as SettingsItemModel
        (viewHolder as SettingViewHolder).bind(currentItem, position, itemCount)
    }

    override fun isForViewType(item: ItemModel, position: Int): Boolean =
        item is SettingsItemModel

    override fun getViewType(): Int = R.layout.settings_item

    inner class SettingViewHolder(
        private val binding: SettingsItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SettingsItemModel, position: Int, itemsCount: Int) {
            binding.parent.setOnClickListener {
                itemListener.invoke(item)
            }
            binding.optionTv.text = item.option

            if(item.isActive){

            } else {

            }
        }
    }
}
