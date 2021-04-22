package com.basis.base.recyclerview.adapter.holder

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

import com.basis.base.recyclerview.adapter.binder.MultiTypeBinder


class MultiTypeViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root), AutoCloseable {

    private var mAlreadyBinding: MultiTypeBinder<ViewDataBinding>? = null

    /**
     * 绑定Binder
     */
    fun onBindViewHolder(items: MultiTypeBinder<ViewDataBinding>) {
        // 如果两次绑定的 Binder 不一致，则直接销毁
        if (mAlreadyBinding != null && items !== mAlreadyBinding) close()
        // 开始绑定
        items.bindViewDataBinding(binding)
        // 保存绑定的 Binder
        mAlreadyBinding = items
    }

    /**
     * 销毁绑定的Binder
     */
    override fun close() {
        mAlreadyBinding?.unbindDataBinding()
        mAlreadyBinding = null
    }
}