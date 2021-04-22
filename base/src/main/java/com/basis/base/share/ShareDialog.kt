package com.basis.base.share

import android.view.View
import android.widget.ImageView

import com.basis.base.R
import com.basis.base.base.BaseDialogFragment

open class ShareDialog : BaseDialogFragment() {

    override fun layoutRes(): Int {
        return R.layout.dialog_share
    }


    override fun bindView(view: View) {
        val imageView: ImageView = view?.findViewById(R.id.tv_share_wx)
        imageView?.setOnClickListener {

        }
    }
}