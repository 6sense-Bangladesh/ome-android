package com.ome.app.presentation.base.recycler

import androidx.recyclerview.widget.DiffUtil

class DefaultDiffItemCallback<DataType: Any> : DiffUtil.ItemCallback<DataType>() {
    override fun areItemsTheSame(oldItem: DataType, newItem: DataType): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: DataType, newItem: DataType): Boolean = false
}
