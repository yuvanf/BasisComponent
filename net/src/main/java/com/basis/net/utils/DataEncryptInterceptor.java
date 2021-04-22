package com.basis.net.utils;

import android.text.TextUtils;

import com.basis.base.utils.LogUtils;

import java.io.IOException;
import java.net.URI;


import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 加解密拦截器
 */
public class DataEncryptInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        //请求
        Request request = chain.request();
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        try {
            RequestBody oldRequestBody = request.body();
            Buffer requestBuffer = new Buffer();
            oldRequestBody.writeTo(requestBuffer);
            String oldBodyStr = requestBuffer.readUtf8();
            LogUtils.Companion.d("RetrofitLog",oldBodyStr==null?"":oldBodyStr);
            requestBuffer.close();
            Headers headers = request.headers();
            RequestBody newBody = RequestBody.create(mediaType, oldBodyStr);
            //构造新的request
            request = request.newBuilder()
                    .headers(headers)
                    .method(request.method(), newBody)
                    .build();
        }catch (Exception e){

        }
        //响应
        Response response = chain.proceed(request);
        //只有约定的返回码才经过加密，才需要走解密的逻辑
        if (response.code() == 200) {
            //解密
            //获取响应头
            try {
                ResponseBody oldResponseBody = response.body();
                String oldResponseBodyStr = oldResponseBody.string();
                //构造新的response
                ResponseBody newResponseBody = ResponseBody.create(mediaType, oldResponseBodyStr);
                response = response.newBuilder().body(newResponseBody).build();
                LogUtils.Companion.d("RetrofitLog",oldResponseBodyStr);
            }catch (Exception e){
                LogUtils.Companion.d("RetrofitLog","e"+e.getMessage());
            }finally {
                response.close();
            }
        }
        //返回
        return response;
    }

    /**
     * 是否是his客户端接口
     * @param url
     * @return
     */
    private boolean isHis(HttpUrl url){
        if (url!=null){
            URI uri = url.uri();
            if (uri!=null){
                String path = uri.getPath();
                if (!TextUtils.isEmpty(path) && path.contains("/his/")){
                    return true;
                }
            }
        }
        return false;
    }



}
