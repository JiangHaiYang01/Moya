package com.allens.moya.request

import com.allens.moya.manager.HttpManager

open class BasicDownLoadRequest : DownLoadRequest() {
    lateinit var manager: HttpManager
}