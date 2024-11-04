@file:Suppress("UNCHECKED_CAST")

package com.ome.app.presentation.base.recycler

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback

class DiffUtilDelegationCallback private constructor(
    private val oldList: List<ItemModel>,
    private val newList: List<ItemModel>,
    private val delegates: Map<Class<ItemModel>, ItemCallback<ItemModel>>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return withItems(
            oldItemPosition = oldItemPosition,
            newItemPosition = newItemPosition,
            defaultValue = ::areItemsTheSame,
            block = ItemCallback<ItemModel>::areItemsTheSame
        )
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return withItems(
            oldItemPosition = oldItemPosition,
            newItemPosition = newItemPosition,
            defaultValue = ::areContentsTheSame,
            block = ItemCallback<ItemModel>::areContentsTheSame
        )
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return withItems(
            oldItemPosition = oldItemPosition,
            newItemPosition = newItemPosition,
            defaultValue = ::getChangePayload,
            block = ItemCallback<ItemModel>::getChangePayload
        )
    }

    private fun areItemsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
        return oldItem::class.java == newItem::class.java
    }

    private fun areContentsTheSame(oldItem: ItemModel, newItem: ItemModel): Boolean {
        return oldItem == newItem
    }

    private fun getChangePayload(oldItem: ItemModel, newItem: ItemModel): Any? {
        return null
    }

    private inline fun <T> withItems(
        oldItemPosition: Int,
        newItemPosition: Int,
        defaultValue: (oldItem: ItemModel, newItem: ItemModel) -> T,
        block: (delegate: ItemCallback<ItemModel>, oldItem: ItemModel, newItem: ItemModel) -> T
    ): T {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        val oldItemClass = oldItem::class.java
        if (oldItemClass != newItem::class.java) {
            return defaultValue(oldItem, newItem)
        }

        val delegate = delegates[oldItemClass] ?: return defaultValue(oldItem, newItem)
        return block(delegate, oldItem, newItem)
    }

    class Builder {

        private val delegates: MutableMap<Class<ItemModel>, ItemCallback<ItemModel>> =
            mutableMapOf()

        inline fun <reified T : ItemModel> delegate(itemCallback: ItemCallback<T>) =
            addDelegate(T::class.java, itemCallback)

        fun <T : ItemModel> addDelegate(clazz: Class<T>, itemCallback: ItemCallback<T>): Builder {
            delegates[clazz as Class<ItemModel>] = itemCallback as ItemCallback<ItemModel>
            return this
        }

        fun build(oldList: List<ItemModel>, newList: List<ItemModel>) =
            DiffUtilDelegationCallback(ArrayList(oldList), ArrayList(newList), HashMap(delegates))
    }
}
