package com.allens.moya_coroutines.request

import com.allens.moya.request.BasicDownLoadRequest
import kotlinx.coroutines.CoroutineScope

class CoroutinesDownLoadRequest : BasicDownLoadRequest() {
    lateinit var coroutines: CoroutineScope
}