package com.ome.app.ui.dashboard.settings.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ome.app.R
import com.ome.app.databinding.SettingsTitleItemBinding
import com.ome.app.ui.base.recycler.AdapterDelegate
import com.ome.app.ui.base.recycler.ItemModel


class SettingsTitleItemAdapter(
    context: Context
) : AdapterDelegate(context) {

    override fun onCreateViewHolder(parent: ViewGroup): SettingTitleViewHolder =
        SettingTitleViewHolder(
            SettingsTitleItemBinding.inflate(
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
        val currentItem = item as SettingsTitleItemModel
        (viewHolder as SettingTitleViewHolder).bind(currentItem, position, itemCount)
    }

    override fun isForViewType(item: ItemModel, position: Int): Boolean =
        item is SettingsTitleItemModel

    override fun getViewType(): Int = R.layout.settings_title_item

    inner class SettingTitleViewHolder(
        private val binding: SettingsTitleItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SettingsTitleItemModel, position: Int, itemsCount: Int) {
            binding.titleTv.text = item.title
        }
    }
}
