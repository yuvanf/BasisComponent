package com.basis.base.utils;

import android.text.TextUtils;

public class StringUtils {

    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static String togetherStrings(String... strings) {
        if (strings == null || strings.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder("");
        for (String index : strings) {
            if (!isEmpty(index)) {
                stringBuilder.append(index);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 姓名用*隐藏
     */
    public static String hideName(String name) {
        if (isEmpty(name)) {
            return "";
        }
        try {
            char[] r = name.toCharArray();
            if (r.length == 1) {
                return name;
            }
            if (r.length > 1) {
                name = name.replaceFirst(name.substring(1, 2), "*");
            }
        } catch (Exception e) {

        }
        return name;
    }

    /**
     * 身份证用*隐藏
     */
    public static String hideIdCard(String idCardNum) {
        if (isEmpty(idCardNum)) {
            return "";
        }
        try {
            char[] r = idCardNum.toCharArray();
            if (r.length == 1 || r.length < 9) {
                return idCardNum;
            }
            if (r.length > 9) {
                idCardNum = idCardNum.replaceFirst(idCardNum.substring(4, r.length - 4), "****");
            }
        } catch (Exception e) {

        }
        return idCardNum;
    }

    /**
     * 电话号码用*隐藏
     */
    public static String hidePhone(String phone) {
        if (isEmpty(phone)) {
            return "";
        }
        try {
            char[] r = phone.toCharArray();
            if (r.length == 1 || r.length < 8) {
                return phone;
            }
            if (r.length > 8) {
                phone = phone.replaceFirst(phone.substring(3, r.length - 4), "****");
            }
        } catch (Exception e) {

        }
        return phone;
    }

    /**
     * 字符串用*隐藏 大于6位
     */
    public static String hideNumber(String number) {
        if (isEmpty(number)) {
            return "";
        }
        if (number.length() < 6) {
            return number;
        }
        StringBuffer stringBuffer = new StringBuffer("*");
        int size = number.length() - 6;
        for (int i = 0; i < size; i++) {
            stringBuffer.append("*");
        }
        try {
            char[] r = number.toCharArray();
            number = number.replaceFirst(number.substring(2, r.length - 3), stringBuffer.toString());

        } catch (Exception e) {

        }
        return number;
    }


    /**
     * @param text
     * @return
     */
    public static int secureInteger(String text) {
        return secureInteger(text, 0);
    }

    public static int secureInteger(String text, int defaultValue) {

        if (TextUtils.isEmpty(text)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
        }
        return defaultValue;
    }

    public static long secureLong(String text) {

        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double secureDouble(String text) {

        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static float secureFloat(String text) {

        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
