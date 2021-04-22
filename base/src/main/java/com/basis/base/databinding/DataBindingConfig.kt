package com.basis.base.databinding

import android.util.SparseArray
import androidx.lifecycle.ViewModel

 class DataBindingConfig(var layout: Int, var vmVariableId: Int, var stateViewModel: ViewModel) {

    private val bindingParams: SparseArray<Any> = SparseArray()

    fun getBindingParams(): SparseArray<*>? {
        return bindingParams
    }

    fun addBindingParam(
        variableId: Int,
        obj: Any
    ): DataBindingConfig {
        if (bindingParams[variableId] == null) {
            bindingParams.put(variableId, obj)
        }
        return this
    }
}