package com.basis.base.share.weixin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.text.TextUtils

import com.basis.base.base.BaseApplication
import com.basis.base.constant.ModelConstant
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelmsg.*
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.ByteArrayOutputStream

class WeiXinManager {

    companion object{

        var weixinApi: IWXAPI? = null

        /**
         * 初始化微信
         */
        @JvmStatic
        fun initWeiXin() {
            if (weixinApi==null){
                weixinApi =
                    WXAPIFactory.createWXAPI(BaseApplication.context, ModelConstant.weixinAppId, true);
                weixinApi?.registerApp(ModelConstant.weixinAppId)
                BaseApplication.context?.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        // 将该app注册到微信
                        weixinApi?.registerApp(ModelConstant.weixinAppId)
                    }
                }, IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP))
            }
        }

        /**
         * 请求登录微信
         */
        @JvmStatic
        fun requestLoginWX() {
            if (!AppUtils.isAppInstalled(ModelConstant.weixinPackName)){
                ToastUtils.showShort("请安装微信")
                return
            }
            val req = SendAuth.Req()
            req.scope = "snsapi_userinfo"
            req.state = "hesport"
            weixinApi?.sendReq(req)
        }


        /**
         * 分享文字信息
         * type发送的目标场景
         * 分享到对话:
         * SendMessageToWX.Req.WXSceneSession
         *分享到朋友圈:
         * SendMessageToWX.Req.WXSceneTimeline ;
         *分享到收藏:
         * SendMessageToWX.Req.WXSceneFavorite
         */
        @JvmStatic
        fun shareText(text: String, type: Int) {
            val wxTextObject = WXTextObject()
            wxTextObject.text = text
            val msg = WXMediaMessage()
            msg.mediaObject = wxTextObject
            msg.description = text
            toShare(msg, type,"text")
        }


        /**
         * 分享图片
         */
        @JvmStatic
        fun shareImage(bitmap: Bitmap, type: Int) {
            bitmap?.let {
                val imgObj = WXImageObject(bitmap)
                val msg = WXMediaMessage()
                msg.mediaObject = imgObj
                val smBitmap = Bitmap.createScaledBitmap(it, 100, 100, true)
                it.recycle()
                msg.thumbData = bmpToByteArray(smBitmap)
                toShare(msg, type,"image")
            }
        }

        /**
         * 分享图片
         */
        @JvmStatic
        fun shareImage(path: String, type: Int) {
            val imgObj = WXImageObject()
            val msg = WXMediaMessage()
            imgObj.imagePath = path
            msg.mediaObject = imgObj
            toShare(msg, type,"image")
        }

        /**
         * 分享视频
         * path 视频链接
         * title 视频标题
         * description 视频描述
         * bitmap 缩略图
         */
        @JvmStatic
        fun shareVideo(path: String, title:String,description:String,bitmap: Bitmap,type: Int) {
            val video = WXVideoObject()
            video.videoUrl = path
            val msg = WXMediaMessage()
            msg.title=title
            msg.description=description
            bitmap?.let {
                msg.thumbData=bmpToByteArray(it)
                it.recycle()
            }
            toShare(msg,type,"video")
        }

        /**
         * 调用微信分享
         */
        @JvmStatic
        fun toShare(wxObject: WXMediaMessage, type: Int,shareType:String) {
            wxObject?.let {
                val req = SendMessageToWX.Req()
                req.transaction = buildTransaction("shareType")
                req.message = it
                req.scene = type
                weixinApi?.sendReq(req)
            }
        }

        /**
         * 构建一个唯一标志
         */
        @JvmStatic
        private   fun buildTransaction(type: String): String = if (TextUtils.isEmpty(type)) {
            System.currentTimeMillis().toString()
        } else {
            type.plus(System.currentTimeMillis())
        }

        /**
         * bitmap转byte[]
         */
        @JvmStatic
        private fun bmpToByteArray(bitmap: Bitmap): ByteArray {
            val output = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            val result = output.toByteArray()
            output.close()
            return result
        }
    }


}