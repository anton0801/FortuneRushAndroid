package com.enastroekmozhnov.common

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class RVAdapter<T>(
    private val onCreateVH: (ViewGroup, Int) -> BaseViewHolder<T>
) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    private val items = mutableListOf<T>()
    var size = 1500
    var isFull = true
    var onBindAdditionalMethod: ((BaseViewHolder<T>, Int, T) -> Unit)? = null

    fun setItems(items: List<T>) {
        this.items.clear()
        notifyDataSetChanged()
        items.forEach { item ->
            if (!this.items.contains(item)) {
                this.items.add(item)
                notifyItemInserted(this.items.size - 1)
            }
        }
    }

    fun addItem(item: T) {
        if (!items.contains(item)) {
            items.add(item)
            notifyItemInserted(items.size - 1)
        }
    }

    fun updateItem(oldItem: T, newItem: T) {
        val pos = items.indexOf(oldItem)
        items[pos] = newItem
        notifyItemChanged(pos)
    }

    fun updateItems() {
        notifyDataSetChanged()
    }

    fun getItems(): List<T> = items

    fun removeItem(item: T) {
        val pos = items.indexOf(item)
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return onCreateVH(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        if (items.size > 0) {
            val item = if (position == 0) items[0] else items[position % items.size]
            onBindAdditionalMethod?.invoke(holder, position, item)
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int = if (isFull) size else items.size

}

open class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(item: T) {}
}
