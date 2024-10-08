package com.ome.app.ui.dashboard.profile.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ome.Ome.R
import com.ome.Ome.databinding.MessageItemBinding
import com.ome.app.ui.base.recycler.AdapterDelegate
import com.ome.app.ui.base.recycler.ItemModel
import com.ome.app.utils.makeGone
import com.ome.app.utils.makeVisible


class MessageAdapter(
    context: Context,
    private val itemListener: (MessageItemModel) -> Unit
) : AdapterDelegate(context) {

    override fun onCreateViewHolder(parent: ViewGroup): MessageViewHolder =
        MessageViewHolder(
            MessageItemBinding.inflate(
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
        val currentItem = item as MessageItemModel
        (viewHolder as MessageViewHolder).bind(currentItem, position, itemCount)
        viewHolder.itemView.setOnClickListener { itemListener.invoke(currentItem) }
    }

    override fun isForViewType(item: ItemModel, position: Int): Boolean =
        item is MessageItemModel

    override fun getViewType(): Int = R.layout.message_item

    inner class MessageViewHolder(
        private val binding: MessageItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MessageItemModel, position: Int, itemsCount: Int) {
            binding.messageTv.text = "\"${item.invitationFrom} Send invitation...\""
            if(item.isRead){
                binding.circleIv.makeGone()
                binding.messageTv.setTypeface(binding.messageTv.typeface, Typeface.BOLD)
            } else {
                binding.circleIv.makeVisible()
                binding.messageTv.setTypeface(binding.messageTv.typeface, Typeface.NORMAL)
            }
        }
    }
}
