package com.basis.base.recyclerview
import androidx.recyclerview.widget.RecyclerView

class BannerLayoutManager : RecyclerView.LayoutManager(){

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
       return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
           RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        recycler?.let {
            var offsetx=0
            for (index in 0..itemCount-1){
              val view=  recycler.getViewForPosition(index)
                addView(view)
                measureChildWithMargins(view,0,0)
                var width=getDecoratedMeasuredWidth(view)
                var high=getDecoratedMeasuredHeight(view)
                layoutDecorated(view,offsetx,offsetx+width,0,high)
                offsetx+=width
            }
        }


    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        offsetChildrenHorizontal(-dx)
        return dx
    }
}