package com.allens.moya_coroutines.request

import com.allens.moya.result.DownLoadDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

class CoroutinesDownLoadDisposable(private val job: CoroutineScope) : DownLoadDisposable() {
    override val isDisposed
        get() = !job.isActive

    override fun dispose() {
        if (isDisposed) return
        job.cancel()
    }


}