package com.basis.net



open class RetrofitApiService {

    companion object{

         lateinit var baseApiService: BaseApiService

        fun getApiService():BaseApiService{
            if (!this::baseApiService.isInitialized){
                val retrofit = RetrofitManager.instance.getRetrofit(NetConstant.baseUrl)
                if (retrofit!=null){
                    baseApiService= retrofit.create(BaseApiService::class.java)
                }
            }
            if (baseApiService==null){
                val retrofit = RetrofitManager.instance.getRetrofit(NetConstant.baseUrl)
                if (retrofit!=null){
                    baseApiService= retrofit.create(BaseApiService::class.java)
                }
            }
            return baseApiService
        }

    }
}