package com.basis.base.utils

import android.util.Log
import com.basis.base.BuildConfig


open class LogUtils {

    companion object{

        val isDebug= BuildConfig.isDebug

        fun v(msg: String) {

        }

        fun d(msg: String) {

        }

        fun d(tag: String, msg: String) {
            msg(tag,msg)
        }

        fun msg(tag: String, msg: String){
            if (isDebug){
                msg?.let {
                    Log.d(tag, msg)
                }
            }

        }
    }


}