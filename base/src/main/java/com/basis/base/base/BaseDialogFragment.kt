package com.basis.base.base

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

import com.basis.base.R


abstract class BaseDialogFragment : DialogFragment() {
    private val  TAG:String = "DialogFragment"
    private var window: Window? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(getStyle(), R.style.BottomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(cancelOutside())
        val v = inflater.inflate(layoutRes(), container, false)
        bindView(v)
        return v
    }


    abstract fun layoutRes(): Int
    abstract fun bindView(view: View)
    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            val params = it.attributes
            if (animations() != -1) {
                it.setWindowAnimations(animations())
            }
            params.dimAmount = 0.2f
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            if (width() > 0) {
                params.width = width()
            } else if (isMatchWidth() && marginLeft() == -1) {
                params.width = WindowManager.LayoutParams.MATCH_PARENT
            } else {
                params.width = WindowManager.LayoutParams.WRAP_CONTENT
            }
            if (height() > 0) {
                params.height = height()
            } else {
                params.height = WindowManager.LayoutParams.WRAP_CONTENT
            }
            if (gravity() != -1) {
                params.gravity = gravity()
                if (gravity() == Gravity.BOTTOM || gravity() == Gravity.BOTTOM or Gravity.RIGHT) {
                    if (marginBottom() != -1) {
                        params.y = marginBottom()
                    }
                }
            }
            if (showMarginBottom() != -1) {
                params.y = showMarginBottom()
            }
            if (marginLeft() != -1) {
                params.x = marginLeft()
            }
            if (isTransparent()) {
                params.dimAmount = 0.0f
            }
            it.attributes = params
        }

    }

    open fun getStyle():Int{
        return STYLE_NO_TITLE;
    }

    override fun onDestroy() {
        super.onDestroy()
        if (window != null) {
            window = null
        }
    }

    /**
     * 是否透明背景
     */
    open fun isTransparent():Boolean{
        return false
    }


    /**
     * 宽度是否占满
     * @return
     */
    open fun isMatchWidth():Boolean{
        return true
    }


    /**
     * 设置高度
     * @return
     */
    open fun height():Int{
        return -1
    }

    /**
     * 主题
     */
    open fun animations():Int{
        return -1
    }

    open  fun showMarginBottom():Int{
        return -1
    }


    /**
     * 设置宽度
     * @return
     */
    open fun width():Int{
        return -1
    }


    /**
     * 重写 设置对齐方式
     * @return
     */
    open fun gravity():Int{
        return  Gravity.BOTTOM
    }

    /**
     * 重写 设置距离底部的距离（Gravity.BOTTOM有效）
     * @return
     */
    open fun marginBottom():Int{
        return -1
    }

    open fun marginLeft():Int{
        return -1
    }

    /**
     * 是否可以取消
     */
    open fun cancelOutside():Boolean{
        return true
    }

    open fun show(fragmentManager: FragmentManager?) {
        show(fragmentManager!!, TAG)
    }
}