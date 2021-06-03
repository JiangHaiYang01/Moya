package com.allens.moya.request

import com.allens.moya.impl.OnDownLoadListener


fun DownLoadRequest.getKey(): String {
    return this.tag ?: this.url
}

// 下载请求配置
open class DownLoadRequest {
    lateinit var url: String
    var path: String? = null
    var name: String? = null
    var tag: String? = null
    var listener: OnDownLoadListener? = null

    class Builder {
        private var path: String? = null
        private var name: String? = null
        private var tag: String? = null
        private var listener: OnDownLoadListener? = null

        fun path(path: String) = apply {
            this.path = path
        }

        fun name(name: String) = apply {
            this.name = name
        }

        fun tag(tag: String) = apply {
            this.tag = tag
        }

        fun listener(listener: OnDownLoadListener) = apply {
            this.listener = listener
        }

        fun build(url: String): DownLoadRequest {
            return DownLoadRequest().apply {
                this.url = url
                this.path = this@Builder.path
                this.tag = this@Builder.tag
                this.name = this@Builder.name
                this.listener = this@Builder.listener
            }
        }
    }
}