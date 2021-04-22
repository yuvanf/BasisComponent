package com.basis.base.dialog

import android.view.Gravity
import android.view.View
import com.basis.base.R
import com.basis.base.base.BaseDialogFragment


class LoadingDialog : BaseDialogFragment() {

    override fun layoutRes(): Int {
        return R.layout.dialog_loading
    }

    override fun bindView(view: View) {

    }

    override fun gravity(): Int {
        return Gravity.CENTER
    }

    override fun isMatchWidth(): Boolean {
        return false
    }


    override fun animations(): Int {
        return R.style.dialogWindowAlphaAnim
    }


}