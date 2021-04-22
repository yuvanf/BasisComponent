package com.basis.net.utils;

import android.net.ParseException;

import com.basis.net.model.ApiResponse;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;


public class ExceptionFactory {

    public static ApiResponse analysisException(Throwable e, ApiResponse response) {
        if (e instanceof HttpException) {
            //*网络异常*//*
            response.setErrorCode(NetEnum.NetException.getId());
            response.setErrorMsg(NetEnum.NetException.getMessage());
        } else if (e instanceof SocketTimeoutException) {
            response.setErrorCode(NetEnum.TimeOutException.getId());
            response.setErrorMsg(NetEnum.TimeOutException.getMessage());
        } else if (e instanceof JSONException || e instanceof ParseException || e instanceof MalformedJsonException) {
            response.setErrorCode(NetEnum.ParseException.getId());
            response.setErrorMsg(NetEnum.ParseException.getMessage());
        } else if (e instanceof UnknownHostException) {
            response.setErrorCode(NetEnum.UnKnowHostException.getId());
            response.setErrorMsg(NetEnum.UnKnowHostException.getMessage());
        } else if (e instanceof ConnectException) {
            response.setErrorCode(NetEnum.CONNECTException.getId());
            response.setErrorMsg(NetEnum.CONNECTException.getMessage());
        } else {
            response.setErrorCode(NetEnum.ApiException.getId());
            response.setErrorMsg(NetEnum.ApiException.getMessage());
        }

        return response;
    }
}
