package com.basis.net


import com.basis.net.listener.IApiRequest
import com.basis.net.model.ApiResponse
import com.basis.net.utils.GsonConvertUtils
import com.basis.net.utils.ParseUtils
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse


open class ApiRequest<T> : IApiRequest {

    var mRequestData: Any? = null

    var mAppendPath: String = ""

    var mClazz: Class<*>? = null

    /**
     * 添加请求参数 （json提交）
     */
    fun setArgs(args: Any): ApiRequest<T> {
        args?.apply {
            mRequestData = this
            if (this is CharSequence) {
                val text = this as String
                mRequestData = GsonConvertUtils.getJsonParser().parse(text)
            } else if (this is JsonElement) {
                mRequestData = GsonConvertUtils.getJsonParser().parse(this.toString())
            }
        }
        return this
    }

    /**
     * 设置接口地址 （json提交，表单提交请在定义方法时写明api路径）
     */
    fun append(url: String): ApiRequest<T> {
        url?.let {
            mAppendPath = it
        }
        return this
    }

    /**
     * json的方式提交
     */
    suspend fun requests(clazz: Class<*>): ApiResponse<T> {
        mClazz = clazz
        return onRequest()
    }

    /**
     * 表单的方式提交
     */
    suspend fun requestsFrom(clazz: Class<*>, call: Call<JsonElement>): ApiResponse<T> {
        mClazz = clazz
        return onRequestFrom(call)
    }

    /**
     * json方式提交 入参转成json
     */
    private suspend fun onRequest(): ApiResponse<T> {
        return if (mRequestData != null) {
            request(
                RetrofitApiService.getApiService()
                    .post(NetConstant.baseUrl.plus(mAppendPath), mRequestData!!)
            )
        } else {
            request(
                RetrofitApiService.getApiService().post(NetConstant.baseUrl.plus(mAppendPath), "{}")
            )
        }
    }

    /**
     * 表单的方式提交直接提交
     */
    private suspend fun onRequestFrom(call: Call<JsonElement>): ApiResponse<T> {
        return request(call)
    }

    /**
     * 请求网络
     */
    private suspend fun request(call: Call<JsonElement>): ApiResponse<T> {
        return withContext(Dispatchers.IO) {
            val await = call.awaitResponse()
            if (await.isSuccessful) {
                onParseSuccessful(await)
            } else {
                var response = ApiResponse<T>()
                try {
                    await.errorBody()?.let {
                        val errorString = it.string()
                        val json = JsonParser().parse(errorString)
                        if (json!=null){
                            getResponseDatas(true,json,response)
                        }
                    }
                } catch (e: Exception) {

                }
                response.code = -1
                response
            }
        }
    }


    /**
     * 接口成功返回
     */
    private fun onParseSuccessful(response: Response<JsonElement>): ApiResponse<T> {
        response?.body()?.let {
           return getResponseDatas(false,it,ApiResponse<T>())
        }
        return ApiResponse()
    }

    /**
     * 解析接口返回的json数据 组装成ApiResponse
     */
    fun getResponseDatas(isError:Boolean,json:JsonElement,apiResponse:ApiResponse<T>):ApiResponse<T>{
        return apiResponse.apply {
            if (json.isJsonObject) {
                val asJsonObject = json.asJsonObject
                asJsonObject?.let { it1 ->
                    if (it1.has("msg")) {
                        this.message = it1.get("msg").asString
                    }
                    if (it1.has("message")) {
                        this.message = it1.get("message").asString
                    }
                    if (it1.has("code")) {
                        this.code = it1.get("code").asInt
                    }
                    if (!isError && it1.has("data")) {
                        val jsonElement = it1.get("data")
                        if (!jsonElement.isJsonNull){
                            jsonElement?.apply {
                                ParseUtils.parseJsonElement(this, mClazz, "")?.apply {
                                    apiResponse.data = this as T
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    override fun cancel() {

    }


}