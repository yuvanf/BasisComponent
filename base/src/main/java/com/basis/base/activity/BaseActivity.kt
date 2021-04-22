package com.basis.base.activity

import android.os.Bundle



open abstract class BaseActivity : CommonActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getLayout()!=-1){
            setContentView(getLayout())
        }
        init()
    }


    /**
     * 获取布局
     *  isUseDataBing 返回true 这里可以随便返回什么都可以
     */
    abstract fun getLayout(): Int

    /**
     * 初始化
     */
    abstract fun init()

}