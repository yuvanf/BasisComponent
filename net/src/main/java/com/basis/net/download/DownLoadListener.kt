package com.basis.net.download

interface DownLoadListener {
    fun onSuccess()
    fun onFailure()
    fun onStart()

    /**
     *
     * @param progress  进度 最大100
     * @param progressStatus  容量进度 例如  10M/90M
     * @param speed 下载速度  例如  1M/s
     */
    fun onProgress(
        progress: Float,
        progressStatus: String,
        speed: String
    )
}