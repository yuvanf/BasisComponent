package com.basis.base.utils

import android.content.Context
import java.lang.reflect.Method

class DeviceUtils {
    companion object{
        fun hasNotch(context: Context): Boolean {
            var ret = false
            try {
                val cl: ClassLoader = context.getClassLoader()
                val HwNotchSizeUtil =
                    cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
                val get: Method = HwNotchSizeUtil.getMethod("hasNotchInScreen")
                ret = get.invoke(HwNotchSizeUtil) as Boolean
            } catch (e: ClassNotFoundException) {
                LogUtils.d("test", "hasNotchInScreen ClassNotFoundException")
            } catch (e: NoSuchMethodException) {
                LogUtils.d("test", "hasNotchInScreen NoSuchMethodException")
            } catch (e: Exception) {
                LogUtils.d("test", "hasNotchInScreen Exception")
            } finally {
                return ret
            }
        }


        fun miHasNotch(context: Context?): Boolean {
            var ret = false
            try {
                if (context!=null){
                    val cl = context.classLoader
                    val SystemProperties =
                        cl.loadClass("android.os.SystemProperties")
                    val get = SystemProperties.getMethod(
                        "getInt",
                        String::class.java,
                        Int::class.javaPrimitiveType
                    )
                    ret = get.invoke(SystemProperties, "ro.miui.notch", 0) as Int == 1
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                return ret
            }
        }

        fun vivoHasNotch(context: Context): Boolean {
            var ret = false
            try {
                val classLoader = context.classLoader
                val FtFeature = classLoader.loadClass("android.util.FtFeature")
                val method =
                    FtFeature.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
                ret = method.invoke(FtFeature, "0x00000020") as Boolean
            } catch (e: ClassNotFoundException) {
                LogUtils.d("Notch", "hasNotchAtVivo ClassNotFoundException")
            } catch (e: NoSuchMethodException) {
                LogUtils.d("Notch", "hasNotchAtVivo NoSuchMethodException")
            } catch (e: java.lang.Exception) {
                LogUtils.d("Notch", "hasNotchAtVivo Exception")
            } finally {
                return ret
            }
        }

        fun opHasNotch(context: Context): Boolean {
            return context.packageManager
                .hasSystemFeature("com.oppo.feature.screen.heteromorphism")
        }
    }

}