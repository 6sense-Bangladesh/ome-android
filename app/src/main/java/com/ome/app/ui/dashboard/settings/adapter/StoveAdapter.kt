package com.ome.app.ui.dashboard.settings.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ome.app.R
import com.ome.app.databinding.StoveItemBinding
import com.ome.app.ui.base.recycler.AdapterDelegate
import com.ome.app.ui.base.recycler.ItemModel


class StoveAdapter(
    context: Context,
    private val itemListener: (StoveItemModel) -> Unit
) : AdapterDelegate(context) {

    override fun onCreateViewHolder(parent: ViewGroup): StoveViewHolder =
        StoveViewHolder(
            StoveItemBinding.inflate(
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
        val currentItem = item as StoveItemModel
        (viewHolder as StoveViewHolder).bind(currentItem, position, itemCount)
        viewHolder.itemView.setOnClickListener { itemListener.invoke(currentItem) }
    }

    override fun isForViewType(item: ItemModel, position: Int): Boolean =
        item is StoveItemModel

    override fun getViewType(): Int = R.layout.stove_item

    inner class StoveViewHolder(
        private val binding: StoveItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StoveItemModel, position: Int, itemsCount: Int) {
            binding.stoveTv.text = item.stove
        }
    }
}
