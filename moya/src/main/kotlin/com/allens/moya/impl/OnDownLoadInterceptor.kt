package com.allens.moya.impl

import com.allens.moya.request.BasicDownLoadRequest


interface OnDownLoadInterceptor {
    fun onIntercept(request: BasicDownLoadRequest,function:(Throwable)->Unit): Boolean
}