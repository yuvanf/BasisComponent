package com.basis.base.share.weixin

import com.basis.base.share.weixin.WeiXinManager


open class WeiXinUtils {

    companion object {
        @Volatile
        private var instance: WeiXinManager? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: WeiXinManager().also { instance = it }
            }
    }

}