package com.basis.net.model

import android.text.TextUtils

class ApiResponse<T> {

    var message: String? = null
    var data: T? = null
    var code: Int? = null
    var errorCode: Int? = null
    var errorMsg: String? = null

    fun getTipMessage(): String {
        if (!TextUtils.isEmpty(message)){
            return message.toString()
        }else if (!TextUtils.isEmpty(errorMsg)){
            return errorMsg.toString()
        }
        return "接口异常"
    }


}



