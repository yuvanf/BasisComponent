package com.basis.net.download

import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.basis.base.utils.LogUtils

import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.StringUtils
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator
import com.liulishuo.okdownload.core.Util
import com.liulishuo.okdownload.core.breakpoint.BlockInfo
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend.Listener4SpeedModel
import java.io.File

/**
 * 下载上传管理类
 */
class LoadManager {

    /**
     * 下载
     * url 下载地址
     * fileName 下载的文件名（带格式）
     */
    class DownloadBuilder(private val url: String,private var fileName: String) {

        /**
         * 下载的目录
         */
        private var fileDir: String = PathUtils.getExternalDownloadsPath()
        private var mTask: DownloadTask? = null
        private var listener: DownLoadListener? = null

        fun setFileDir(fileDir: String): DownloadBuilder {
            this.fileDir = fileDir
            return this
        }

        fun setListener(listener: DownLoadListener): DownloadBuilder {
            this.listener = listener
            return this
        }

        private var readableTotalLength: String? = null
        private var totalLength: Long = 0
        val files: File?
            get() = if (mTask != null) {
                mTask?.file
            } else null

        fun load(): DownloadBuilder {
            if (StringUtils.isEmpty(fileName) || StringUtils.isEmpty(url) || StringUtils.isEmpty(fileDir)) {
                return this
            }
            if (TextUtils.isEmpty(fileDir)){
                fileDir=PathUtils.getInternalAppCachePath()
            }
            val file = File(fileDir)
            if (!file.exists()) {
                return this
            }
            if (mTask == null) {
                mTask = DownloadTask.Builder(url, file)
                    .setFilename(fileName) // 下载进度回调的间隔时间（毫秒）
                    .setMinIntervalMillisCallbackProcess(30) // 任务过去已完成是否要重新下载
                    .setPassIfAlreadyCompleted(false)
                    .build()
            }
            //异步调用
            mTask?.enqueue(object : DownloadListener4WithSpeed() {
                override fun taskStart(@NonNull task: DownloadTask) {}
                override fun connectStart(
                    @NonNull task: DownloadTask,
                    blockIndex: Int,
                    @NonNull requestHeaderFields: Map<String, List<String>>
                ) {
                    if (listener != null) {
                        listener?.onStart()
                    }
                }

                override fun connectEnd(
                    @NonNull task: DownloadTask,
                    blockIndex: Int,
                    responseCode: Int,
                    @NonNull responseHeaderFields: Map<String, List<String>>
                ) {
                }

                override fun infoReady(
                    @NonNull task: DownloadTask,
                    @NonNull info: BreakpointInfo,
                    fromBreakpoint: Boolean,
                    @NonNull model: Listener4SpeedModel
                ) {
                    totalLength = info.totalLength
                    readableTotalLength =
                        Util.humanReadableBytes(totalLength, true)
                    LogUtils.d(
                        "mine",
                        "【2、infoReady】当前进度" + info.totalOffset.toFloat() / totalLength * 100 + "%" + "，" + info.toString()
                    )
                }

                override fun progressBlock(
                    @NonNull task: DownloadTask,
                    blockIndex: Int,
                    currentBlockOffset: Long,
                    @NonNull blockSpeed: SpeedCalculator
                ) {}

                override fun progress(
                    @NonNull task: DownloadTask,
                    currentOffset: Long,
                    @NonNull taskSpeed: SpeedCalculator
                ) {
                    val readableOffset =
                        Util.humanReadableBytes(currentOffset, true)
                    val progressStatus = "$readableOffset/$readableTotalLength"
                    val speed = taskSpeed.speed()
                    val percent = currentOffset.toFloat() / totalLength * 100
                    if (listener != null) {
                        listener?.onProgress(percent, progressStatus, speed)
                    }
                }

                override fun blockEnd(
                    @NonNull task: DownloadTask,
                    blockIndex: Int,
                    info: BlockInfo,
                    @NonNull blockSpeed: SpeedCalculator
                ) {
                }

                override fun taskEnd(
                    @NonNull task: DownloadTask,
                    @NonNull cause: EndCause,
                    @Nullable realCause: Exception?,
                    @NonNull taskSpeed: SpeedCalculator
                ) {
                    if (cause == EndCause.COMPLETED) {
                        if (listener != null) {
                            listener?.onSuccess()
                        }
                    } else {
                        if (cause == EndCause.CANCELED) {
                        } else if (cause == EndCause.ERROR) {
                        } else if (cause == EndCause.FILE_BUSY || cause == EndCause.SAME_TASK_BUSY || cause == EndCause.PRE_ALLOCATE_FAILED) {
                        }
                        if (listener != null) {
                            listener?.onFailure()
                        }
                    }
                }
            })
            return this
        }

        /**
         * 任务取消
         */
        fun cancel() {
            mTask?.cancel()
            if (listener != null) {
                listener = null
            }
        }
    }

    /**
     * 暂时不需要，先不写
     */
    class UploadBuilder
}