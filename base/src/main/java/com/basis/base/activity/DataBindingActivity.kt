package com.basis.base.activity

import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.basis.base.databinding.DataBindingConfig


abstract class DataBindingActivity : CommonActivity() {

    private var mBinding: ViewDataBinding? = null

    private var mTvStrictModeTip: TextView? = null

    protected abstract fun initViewModel()


    protected abstract fun getDataBindingConfig(): DataBindingConfig
    protected abstract fun init()

    /**
     * TODO tip: 警惕使用。非必要情况下，尽可能不在子类中拿到 binding 实例乃至获取 view 实例。使用即埋下隐患。
     * 目前的方案是在 debug 模式下，对获取实例的情况给予提示。
     * @return binding
     */
    protected open fun getBinding(): ViewDataBinding? {
        if (isDebug() && mBinding != null) {
            if (mTvStrictModeTip == null) {
                mTvStrictModeTip = TextView(applicationContext)
                mTvStrictModeTip?.alpha = 0.4f
                mTvStrictModeTip?.textSize = 14f
                mTvStrictModeTip?.setBackgroundColor(Color.WHITE)
                mTvStrictModeTip?.text = "R.string.debug_activity_databinding_warning"
                (mBinding?.root as ViewGroup).addView(mTvStrictModeTip)
            }
        }
        return mBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        val dataBindingConfig: DataBindingConfig = getDataBindingConfig()

        //TODO tip: DataBinding 严格模式：
        // 将 DataBinding 实例限制于 base 页面中，默认不向子类暴露，
        // 通过这样的方式，来彻底解决 视图调用的一致性问题，
        // 如此，视图调用的安全性将和基于函数式编程思想的 Jetpack Compose 持平。
        val binding: ViewDataBinding =
            DataBindingUtil.setContentView(this, dataBindingConfig.layout)
        binding.lifecycleOwner = this
        binding.setVariable(
            dataBindingConfig.vmVariableId,
            dataBindingConfig.stateViewModel
        )
        val bindingParams = dataBindingConfig.getBindingParams()
        var i = 0
        val length = bindingParams?.size()
        length?.let {
            while (i < it) {
                binding.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i))
                i++
            }
        }
        mBinding = binding
        init()
    }

    open fun isDebug(): Boolean {
        return applicationContext.applicationInfo != null &&
                applicationContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding?.unbind()
        mBinding = null
    }
}