package com.allens.moya_coroutines.request

import com.allens.moya.result.Disposable
import com.allens.moya.tools.MoyaLogTool
import kotlinx.coroutines.Job

class CoroutinesDisposable(private val job: Job) : Disposable {
    override val isDisposed
        get() = !job.isActive

    override fun dispose() {
        MoyaLogTool.i("CoroutinesDisposable dispose  isActive:$isDisposed")
        if (isDisposed) return
        job.cancel()
    }

}