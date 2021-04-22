package com.basis.base.recyclerview.adapter.callback

import androidx.recyclerview.widget.DiffUtil

import com.basis.base.recyclerview.adapter.binder.MultiTypeBinder


class DiffItemCallback<T : MultiTypeBinder<*>> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.layoutId() == newItem.layoutId()
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.hashCode() == newItem.hashCode() && oldItem.areContentsTheSame(newItem)
    }

    override fun getChangePayload(oldItem: T, newItem: T): Any? {
        return super.getChangePayload(oldItem, newItem)
    }
}