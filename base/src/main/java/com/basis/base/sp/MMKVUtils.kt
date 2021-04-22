package com.basis.base.sp

import android.content.Context
import android.os.Parcelable
import com.tencent.mmkv.MMKV

class MMKVUtils {

    companion object {

        /**
         * 初始化
         */
        @JvmStatic
        fun init(content: Context) {
            MMKV.initialize(content)
        }

        /**
         * 保存数据  除set
         */
        @JvmStatic
        fun encode(key: String, any: Any): Boolean {
            var flag = false
            val mmkv = MMKV.defaultMMKV()
            if (mmkv != null) {
                when (any) {
                    is Int -> {
                        flag = mmkv.encode(key, any)
                    }
                    is String -> {
                        flag = mmkv.encode(key, any)
                    }
                    is Float -> {
                        flag = mmkv.encode(key, any)
                    }
                    is Boolean -> {
                        flag = mmkv.encode(key, any)
                    }
                    is Double -> {
                        flag = mmkv.encode(key, any)
                    }
                    is Long -> {
                        flag = mmkv.encode(key, any)
                    }
                    is Parcelable -> {
                        flag = mmkv.encode(key, any)
                    }
                    is ByteArray -> {
                        flag = mmkv.encode(key, any)
                    }
                }
            }
            return flag
        }

        /**
         * 保存set
         */
        @JvmStatic
        fun encode(key: String, set: Set<String>): Boolean {
            var flag = false
            val mmkv = MMKV.defaultMMKV()
            mmkv?.encode(key, set)
            return flag
        }


        /**
         * 查询Int
         */
        @JvmStatic
        fun decodeInt(key: String): Int {
            val mmkv = MMKV.defaultMMKV()
            if (mmkv != null) {
                return mmkv.decodeInt(key)
            }
            return 0
        }

        /**
         * 查询Int
         */
        @JvmStatic
        fun decodeInt(key: String, defaultValue: Int): Int {
            val mmkv = MMKV.defaultMMKV()
            if (mmkv != null) {
                return mmkv.decodeInt(key, defaultValue)
            }
            return defaultValue
        }

        /**
         * 查询String
         */
        @JvmStatic
        fun decodeString(key: String): String {
            val mmkv = MMKV.defaultMMKV()
            if (mmkv != null) {
                val value = mmkv.decodeString(key)
                if (value != null) {
                    return value
                }
            }
            return ""
        }

        /**
         * 查询String
         */
        @JvmStatic
        fun decodeString(key: String, defaultValue: String): String {
            return MMKV.defaultMMKV()?.let {
                it.decodeString(key, defaultValue)
            } ?: let {
                defaultValue
            }
        }

        /**
         * 查询Bool
         */
        @JvmStatic
        fun decodeBool(key: String): Boolean {
            return MMKV.defaultMMKV()?.let {
                it.decodeBool(key)
            } ?: let {
                false
            }
        }

        /**
         * 查询Bool
         */
        @JvmStatic
        fun decodeBool(key: String, defaultValue: Boolean): Boolean {
            return MMKV.defaultMMKV()?.let {
                it.decodeBool(key, defaultValue)
            } ?: let {
                defaultValue
            }
        }

        /**
         * 查询Long
         */
        @JvmStatic
        fun decodeLong(key: String): Long {
            return MMKV.defaultMMKV()?.let {
                it.decodeLong(key)
            } ?: let {
                0L
            }
        }

        /**
         * 查询Long
         */
        @JvmStatic
        fun decodeLong(key: String, defaultValue: Long): Long {
            return MMKV.defaultMMKV()?.let {
                it.decodeLong(key, defaultValue)
            } ?: let {
                defaultValue
            }
        }

        /**
         * 查询Float
         */
        @JvmStatic
        fun decodeFloat(key: String): Float {
            return MMKV.defaultMMKV()?.let {
                it.decodeFloat(key)
            }?:let {
                0.0f
            }
        }

        /**
         * 查询Float
         */
        @JvmStatic
        fun decodeFloat(key: String, defaultValue: Float): Float {
            return MMKV.defaultMMKV()?.let {
                it.decodeFloat(key, defaultValue)
            }?:let {
                defaultValue
            }
        }

        /**
         * 查询Double
         */
        @JvmStatic
        fun decodeDouble(key: String): Double {
            return MMKV.defaultMMKV()?.let {
                it.decodeDouble(key)
            }?:let {
                0.0
            }
        }

        /**
         * 查询Double
         */
        @JvmStatic
        fun decodeDouble(key: String, defaultValue: Double): Double {
            val mmkv = MMKV.defaultMMKV()
            if (mmkv != null) {
                return mmkv.decodeDouble(key, defaultValue)
            }
            return defaultValue
        }


        /**
         * 查询Parcelable
         */
        @JvmStatic
        fun <T : Parcelable?> decodeParcelable(key: String, tClass: Class<T>): T? {
            val mmkv = MMKV.defaultMMKV()
            if (mmkv != null) {
                return mmkv.decodeParcelable(key, tClass)
            }
            return null
        }

        /**
         * 查询set
         */
        @JvmStatic
        fun decodeStringSet(key: String): Set<String>? {
            val mmkv = MMKV.defaultMMKV()
            if (mmkv != null) {
                return mmkv.decodeStringSet(key)
            }
            return null
        }

        /**
         * 查询Bytes
         */
        @JvmStatic
        fun decodeBytes(key: String): ByteArray? {
            val mmkv = MMKV.defaultMMKV()
            if (mmkv != null) {
                return mmkv.decodeBytes(key)
            }
            return null
        }

        /**
         * 根据key删除数据
         */
        @JvmStatic
        fun removeValueForKey(key: String) {
            val mmkv = MMKV.defaultMMKV()
            if (mmkv != null) {
                mmkv.removeValueForKey(key)
            }
        }

    }

}