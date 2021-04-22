package com.basis.base.recyclerview.adapter
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.basis.base.recyclerview.adapter.binder.MultiTypeBinder
import com.basis.base.recyclerview.adapter.callback.DiffItemCallback
import com.basis.base.recyclerview.adapter.holder.MultiTypeViewHolder


class MultiTypeAdapter constructor(val layoutManager: RecyclerView.LayoutManager,val isLoop:Boolean): RecyclerView.Adapter<MultiTypeViewHolder>() {

    // 使用后台线程通过差异性计算来更新列表
    private val mAsyncListChange by lazy { AsyncListDiffer(this, DiffItemCallback<MultiTypeBinder<*>>()) }

    // 存储 MultiTypeBinder 和 MultiTypeViewHolder Type
    private var mHashCodeViewType = LinkedHashMap<Int, MultiTypeBinder<*>>()

    init {
        setHasStableIds(true)
    }

    fun notifyAdapterChanged(binders: List<MultiTypeBinder<*>>) {
        mHashCodeViewType = LinkedHashMap()
        binders.forEach {
            mHashCodeViewType[it.hashCode()] = it
        }
        mAsyncListChange.submitList(mHashCodeViewType.map { it.value })
    }

    fun notifyAdapterChanged(binder: MultiTypeBinder<*>) {
        mHashCodeViewType = LinkedHashMap()
        mHashCodeViewType[binder.hashCode()] = binder
        mAsyncListChange.submitList(mHashCodeViewType.map { it.value })
    }

    override fun getItemViewType(position: Int): Int {
        val mItemBinder = mAsyncListChange.currentList[getPosition(position)]
        val mHasCode = mItemBinder.hashCode()
        // 如果Map中不存在当前Binder的hasCode，则向Map中添加当前类型的Binder
        if (!mHashCodeViewType.containsKey(mHasCode)) {
            mHashCodeViewType[mHasCode] = mItemBinder
        }
        return mHasCode
    }

    override fun getItemId(position: Int): Long =getPosition(position).toLong()


    override fun getItemCount(): Int = if (isLoop){333333}else{mAsyncListChange.currentList.size}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiTypeViewHolder {
        try {
            return MultiTypeViewHolder(parent.inflateDataBinding(mHashCodeViewType[viewType]?.layoutId()!!))
        }catch (e: Exception){
            throw NullPointerException("不存在${mHashCodeViewType[viewType]}类型的ViewHolder!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: MultiTypeViewHolder, position: Int) {
        val mCurrentBinder = mAsyncListChange.currentList[getPosition(position)] as MultiTypeBinder<ViewDataBinding>
        holder.itemView.tag = mCurrentBinder.layoutId()
        holder.onBindViewHolder(mCurrentBinder)
    }

  private  fun getPosition(position: Int):Int{
        var size=position
        if (isLoop){
            size = position % mAsyncListChange.currentList.size
        }
        return size
    }
}
