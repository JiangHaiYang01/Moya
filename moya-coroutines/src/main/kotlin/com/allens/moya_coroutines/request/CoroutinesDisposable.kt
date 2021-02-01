package com.allens.moya_coroutines.request

import com.allens.moya.result.Disposable
import kotlinx.coroutines.Job

class CoroutinesDisposable(private val job: Job) : Disposable {
    override val isDisposed
        get() = !job.isActive

    override fun dispose() {
        if (isDisposed) return
        job.cancel()
    }

}