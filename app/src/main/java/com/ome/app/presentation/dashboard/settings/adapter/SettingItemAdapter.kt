package com.ome.app.presentation.dashboard.settings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ome.app.R
import com.ome.app.databinding.SettingsItemBinding
import com.ome.app.databinding.SettingsTitleItemBinding
import com.ome.app.presentation.base.recycler.DefaultDiffItemCallback
import com.ome.app.presentation.base.recycler.ItemModel
import com.ome.app.presentation.dashboard.settings.adapter.model.DeviceSettingsItemModel
import com.ome.app.presentation.dashboard.settings.adapter.model.SettingsItemModel
import com.ome.app.presentation.dashboard.settings.adapter.model.SettingsKnobItemModel
import com.ome.app.presentation.dashboard.settings.adapter.model.SettingsTitleItemModel
import com.ome.app.utils.changeVisibility
import com.ome.app.utils.setBounceClickListener
import com.ome.app.utils.toast


class SettingItemAdapter(private val onClick: (ItemModel) -> Unit) :
    ListAdapter<ItemModel, RecyclerView.ViewHolder>(DefaultDiffItemCallback<ItemModel>()) {

    inner class ViewHolderItem(
        private val binding: SettingsItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: ItemModel) {
            binding.parent.setBounceClickListener {
                when (item) {
                    is SettingsItemModel -> {
                        if (item.isActive)
                            onClick(item)
                        else
                            binding.root.context.toast("Operation not allowed")
                    }

                    is SettingsKnobItemModel, is DeviceSettingsItemModel -> onClick(item)
                }
            }

            when (item) {
                is SettingsItemModel -> {
                    binding.optionTv.text = item.option
                    binding.divider.changeVisibility(item.showDivider)
                }

                is SettingsKnobItemModel -> {
                    binding.optionTv.text = binding.root.context.getString(R.string.knob_, item.stovePosition)
                    binding.divider.changeVisibility(item.showDivider)
                }

                is DeviceSettingsItemModel -> {
                    binding.optionTv.text = item.option
                    if(item == DeviceSettingsItemModel.DeleteKnob)
                        binding.optionTv.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
                    binding.optionTv.setCompoundDrawablesRelativeWithIntrinsicBounds(item.icon,0,0,0)
                    binding.optionTv.compoundDrawablePadding = binding.root.context.resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp)
                }
            }

        }
    }

    inner class ViewHolderTitle(
        private val binding: SettingsTitleItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(item: SettingsTitleItemModel) {
            binding.titleTv.text = item.title
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is SettingsTitleItemModel -> 1
            else -> 2
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> ViewHolderTitle(SettingsTitleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> ViewHolderItem(SettingsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            when (holder.itemViewType) {
                1 -> (holder as ViewHolderTitle).bindView(item as SettingsTitleItemModel)
                else -> (holder as ViewHolderItem).bindView(item)
            }
        }
    }
}