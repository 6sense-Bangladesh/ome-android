package com.ome.app.ui.base.recycler

import android.content.Context
import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlin.reflect.KClass

// TODO work around types here to be able obtain more specific models and viewholder on delegates
// since now they are very generic - just ViewHolder / ItemModel
@Suppress("unused")
open class RecyclerDelegationAdapter(
    protected val context: Context,
    private val scope: CoroutineScope? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val delegates: MutableList<AdapterDelegate> = ArrayList()
    private val itemTypeToDelegatesMap = SparseArray<AdapterDelegate>()
    protected val items: MutableList<ItemModel> = ArrayList()
    private var recyclerView: RecyclerView? = null
    private var diffJob: Job? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        itemTypeToDelegatesMap[viewType].onCreateViewHolder(parent)

    override fun getItemCount(): Int =
        items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        itemTypeToDelegatesMap[holder.itemViewType].onBindViewHolder(
            holder,
            items[position],
            position,
            itemCount
        )
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        itemTypeToDelegatesMap[holder.itemViewType].onBindViewHolder(
            holder,
            items[position],
            payloads,
            position,
            itemCount
        )
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        itemTypeToDelegatesMap[holder.itemViewType].onRecycled(holder)
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        val delegate = delegates
            .find { delegate -> delegate.isForViewType(item, position) }
            ?: throw NullPointerException("Delegate for item in position $position not found")

        val viewType = delegate.getViewType()
        itemTypeToDelegatesMap[viewType] = delegate
        return viewType
    }

    fun addDelegate(delegate: AdapterDelegate) {
        delegates.add(delegate)
    }

    fun addDelegates(vararg delegatesToAdd: AdapterDelegate) {
        delegates.addAll(delegatesToAdd)
    }

    fun addDelegates(delegatesToAdd: List<AdapterDelegate>) {
        delegates.addAll(delegatesToAdd)
    }

    fun removeDelegate(delegate: AdapterDelegate) {
        delegates.remove(delegate)
    }

    fun setItems(items: List<ItemModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addItemToEnd(item: ItemModel) {
        val index = items.size
        addItem(index, item)
    }

    fun addItem(index: Int, item: ItemModel) {
        items.add(index, item)
        notifyItemInserted(index)
    }

    fun addItems(item: List<ItemModel>) {
        items.addAll(item)
        notifyDataSetChanged()
    }

    fun removeItem(item: ItemModel) {
        val position = items.indexOf(item)
        if (position >= 0) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun smoothScrollToPosition(position: Int) {
        recyclerView?.smoothScrollToPosition(position)
    }

    fun setItemsWithDiff(
        items: List<ItemModel>,
        callback: DiffUtil.Callback,
        detectMoves: Boolean = true,
        updatesDispatched: (() -> Unit)? = null
    ) {
        if (scope == null) {
            setItemsWithDiffInternal(items, callback, detectMoves, updatesDispatched)
            return
        }
        diffJob?.cancel()
        diffJob = scope.launch {
            val diffResult = withContext(Dispatchers.Default) {
                DiffUtil.calculateDiff(callback, detectMoves)
            }
            this@RecyclerDelegationAdapter.items.clear()
            this@RecyclerDelegationAdapter.items.addAll(items)
            diffResult.dispatchUpdatesTo(this@RecyclerDelegationAdapter)
            updatesDispatched?.invoke()
        }
    }

    private fun setItemsWithDiffInternal(
        items: List<ItemModel>,
        callback: DiffUtil.Callback,
        detectMoves: Boolean = true,
        updatesDispatched: (() -> Unit)? = null
    ) {
        val diffResult = DiffUtil.calculateDiff(callback, detectMoves)
        this@RecyclerDelegationAdapter.items.clear()
        this@RecyclerDelegationAdapter.items.addAll(items)
        diffResult.dispatchUpdatesTo(this@RecyclerDelegationAdapter)
        updatesDispatched?.invoke()
    }

    fun addItemsToEnd(newItems: List<ItemModel>) {
        val lastPosition = this.items.size
        this.items.addAll(newItems)
        this.notifyItemRangeInserted(lastPosition, newItems.size - 1)
    }

    fun getData(): List<ItemModel> = this.items

    @Suppress("UNCHECKED_CAST")
    fun <T : ItemModel> getDataTyped(): List<T> = this.items as List<T>

    fun getItem(position: Int) = this.items[position]

    @Suppress("UNCHECKED_CAST")
    fun <T : ItemModel> getItemTyped(position: Int): T = this.items[position] as T

    fun <T : AdapterDelegate> getDelegatesTyped(kClass: KClass<T>): List<T> {
        return delegates.filterIsInstance(kClass.java)
    }

    fun <T : AdapterDelegate> getDelegateTyped(kClass: KClass<T>): T? {
        return delegates.filterIsInstance(kClass.java).firstOrNull()
    }

    fun clearItems() {
        this.items.clear()
        notifyDataSetChanged()
    }

}
