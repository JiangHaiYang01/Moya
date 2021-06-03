package com.allens.moya.impl


interface OnDownLoadProgressListener {
    /**
     * 下载进度
     *
     * @param key       如果设置了tag 就是tag 如何没设置 就是 url
     * @param progress  进度
     * @param read      读取
     * @param count     总共长度
     * @param done      是否完成
     */
    fun onUpdate(
        key: String,
        progress: Int,
        read: Long,
        count: Long,
        done: Boolean
    ){}
}


interface OnDownLoadListener : OnDownLoadProgressListener {


    /**
     * 等待下载
     * @param key String
     */
    fun onDownLoadPrepare(key: String)

    // 进度
    fun onDownLoadProgress(key: String, progress: Int)

    // 下载失败
    fun onDownLoadError(key: String, throwable: Throwable)

    // 下载成功
    fun onDownLoadSuccess(key: String, path: String)

    // 下载暂停
    fun onDownLoadPause(key: String)

    // 下载取消
    fun onDownLoadCancel(key: String)
}