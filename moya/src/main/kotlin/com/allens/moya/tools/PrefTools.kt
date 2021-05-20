package com.allens.moya.tools


import android.content.Context
import com.allens.moya.request.DownLoadRequest
import com.tencent.mmkv.MMKV


object PrefTools {
    fun init(context: Context) {
        MMKV.initialize(context.applicationContext)
    }

    fun getLong(request: DownLoadRequest): Long {
        return MMKV.defaultMMKV()?.decodeLong(request.url, 0L) ?: 0L
    }

    fun putLong(request: DownLoadRequest, progress: Long) {
        MMKV.defaultMMKV()?.encode(request.url, progress)
    }

    fun remove(request: DownLoadRequest) {
        MMKV.defaultMMKV()?.remove(request.url)
    }
}