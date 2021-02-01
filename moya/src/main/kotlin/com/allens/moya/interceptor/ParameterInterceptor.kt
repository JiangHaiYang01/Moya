package com.allens.moya.interceptor


import android.os.Looper
import com.allens.moya.impl.OnDownLoadInterceptor
import com.allens.moya.request.BasicDownLoadRequest
import com.allens.moya.tools.MoyaLogTool

//参数校验是否合法
class ParameterInterceptor : OnDownLoadInterceptor {
    override fun onIntercept(request: BasicDownLoadRequest): Boolean {
        MoyaLogTool.i("ParameterInterceptor check")
        //在主线程 no
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            MoyaLogTool.i("work in main thread")
            return false
        }
        //下载地址不能是空
        if (request.url.isEmpty()) {
//            error(request, "download url is empty")
            return false
        }
        //保存路径不合法
        if (request.path.isNullOrEmpty()) {
//            error(request, "save file  path is null or empty")
            return false
        }
        //保存名称不合法
        if (request.name.isNullOrEmpty()) {
//            error(request, "save file  name is null or empty")
            return false
        }
        MoyaLogTool.i("ParameterInterceptor pass")
        return true
    }
}