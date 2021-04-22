package com.basis.net.utils;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ParseUtils {
    /**
     * 提交数据的字符编码
     */
    private static final String GET_ENCODE = "utf8";

    /**
     * @param je        需要解析的数据
     * @param mClass    解析成为什么对象
     * @param key       解析的key
     * @param parseTime 已经解析的次数
     */
    public static <T> T parseJsonElement(JsonElement je, Class<T> mClass, String key, int parseTime) {
        try {
            if (je.isJsonArray()) {
                List<T> list = new ArrayList<>();
                T objT;
                JsonArray jsonArray = je.getAsJsonArray();
                JsonObject jsonObject;
                int length = jsonArray.size();
                for (int i = 0; i < length; i++) {
                    JsonElement mJs = jsonArray.get(i);
                    if (mJs.isJsonObject()) {
                        jsonObject = mJs.getAsJsonObject();
                        if (null != jsonObject) {
                            objT = GsonConvertUtils.getGson().fromJson(jsonObject, (Class<T>) mClass);
                            list.add(objT);
                        }
                    } else {

                        list.add((T) mJs.getAsString());
                    }
                }
                //服务端那边底层,当数据为空的时候,默认是数组,这边经常有转换异常
                if (list.isEmpty()) {
                    return null;
                }
                return (T) list;
            } else if (je.isJsonObject()) {
                //如果传进来的是String,就应该是带着一个字段的数据
                if (String.class.equals(mClass)) {
                    return (T) ParseUtils.getStringResult(je, key);
                }
                if (je==null){
                    return null;
                }

                return GsonConvertUtils.getGson().fromJson(je, (Class<T>) mClass);
            } else {
                //需要的是一个对象,但是返回的是一个字符串,此类问题也经常出现,导致崩溃
                if (String.class.equals(mClass)) {
                    return (T) je.getAsString();
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //通用解析错误,去除掉有问题的数据,进行重新解析一遍
            if (parseTime <= 0) {
                String formatString = je.toString();
                if (formatString.contains("[]")) {
                    formatString = formatString.replace("[]", "null");
                }
                if (formatString.contains("[\"\"]")) {
                    formatString = formatString.replace("[\"\"]", "null");
                }
                if (formatString.contains("[null]")) {
                    formatString = formatString.replace("[null]", "null");
                }
                if (formatString.contains("{}")) {
                    formatString = formatString.replace("{}", "null");
                }
                if (formatString.contains("\"\"")) {
                    formatString = formatString.replace("\"\"", "null");
                }
                je = GsonConvertUtils.getJsonParser().parse(formatString);
                return parseJsonElement(je, mClass, key, parseTime + 1);
            }
        }
        return null;
    }

    public static <T> T parseJsonElement(JsonElement je, Class<T> mClass, String key) {
        return parseJsonElement(je, mClass, key, 0);
    }

    /**
     * 组成完整的请求地址
     */
    public static String composeUrl(String baseUrl, HashMap<String, String> mServerParams) {
        // 检查并自动补全地址参数开始符号
        if (baseUrl.indexOf("?") == -1) {
            baseUrl += "?";
        }
        StringBuilder encodedParams = new StringBuilder(baseUrl);
        try {
            for (Map.Entry<String, String> entry : mServerParams.entrySet()) {
                encodedParams.append('&');
                encodedParams.append(URLEncoder.encode(entry.getKey(), GET_ENCODE));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode((String) entry.getValue(), GET_ENCODE));
            }
            return encodedParams.toString();
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Encoding not supported: " + GET_ENCODE, ex);
        }
    }

    public static String getStringResult(JsonElement je, String fieldName) {
        if (TextUtils.isEmpty(fieldName)) {
            return getStringResult(je);
        }
        if (je == null || !je.isJsonObject()) {
            return null;
        }
        try {
            JsonElement je2 = je.getAsJsonObject().get(fieldName);
            if (je2 != null) {
                return je2.getAsString();
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 从服务端返回结果里解析结果字符串，形如 {"statusCode":0,"data":{"result":"取消订单成功"}}<br>
     * 会依次尝试 result/message/msg/success 这些字段名
     *
     * @param je ResponseParser.parse 传进来的参数
     * @return 解析不了返回null
     */
    public static String getStringResult(JsonElement je) {
        String res = getStringResult(je, "result");
        if (res == null) {
            res = getStringResult(je, "message");
        }
        if (res == null) {
            res = getStringResult(je, "msg");
        }
        if (res == null) {
            res = getStringResult(je, "success");
        }
        if (res==null && je !=null){
            res=je.toString();
        }
        return res;
    }

    /**
     *
     * */
    public static HashMap<String, Object> toDatasMaps(Object object) {
        HashMap mMaps = new HashMap();
        mMaps.put("data", object);
        return mMaps;
    }

}
