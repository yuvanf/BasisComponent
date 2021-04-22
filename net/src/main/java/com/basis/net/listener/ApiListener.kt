package com.basis.net.listener


import com.basis.net.model.ApiResponse


/**
 * 服务端API响应监听器
 * @param <T> 返回结果类型
</T> */
interface ApiListener<T> {
    /**
     * @param response
     */
    fun onResponse(response: ApiResponse<T>?)
}