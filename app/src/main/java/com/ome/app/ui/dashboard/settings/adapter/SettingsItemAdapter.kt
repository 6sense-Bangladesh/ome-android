package com.ome.app.ui.dashboard.settings.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ome.app.R
import com.ome.app.databinding.SettingsItemBinding
import com.ome.app.ui.base.recycler.AdapterDelegate
import com.ome.app.ui.base.recycler.ItemModel
import com.ome.app.ui.dashboard.settings.adapter.model.SettingsItemModel
import com.ome.app.ui.dashboard.settings.adapter.model.SettingsKnobItemModel


class SettingsItemAdapter(
    context: Context,
    private val itemListener: (ItemModel) -> Unit
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
        (viewHolder as SettingViewHolder).bind(item, position, itemCount)
    }

    override fun isForViewType(item: ItemModel, position: Int): Boolean =
        item is SettingsItemModel || item is SettingsKnobItemModel

    override fun getViewType(): Int = R.layout.settings_item

    inner class SettingViewHolder(
        private val binding: SettingsItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemModel, position: Int, itemsCount: Int) {
            binding.parent.setOnClickListener {
                itemListener.invoke(item)
            }
            when (item) {
                is SettingsItemModel -> {
                    binding.optionTv.text = item.option
                    if (item.isActive) {

                    } else {

                    }
                }
                is SettingsKnobItemModel -> {
                    binding.optionTv.text = item.name
                }
            }

        }
    }
}
