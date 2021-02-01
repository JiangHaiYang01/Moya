package com.allens.moya.impl

import com.allens.moya.interceptor.ParameterInterceptor
import com.allens.moya.request.BasicDownLoadRequest
import com.allens.moya.request.DownLoadRequest
import com.allens.moya.result.Disposable
import com.allens.moya.tools.FileTool
import com.allens.moya.tools.MoyaLogTool
import com.allens.moya.tools.PrefTools
import okhttp3.ResponseBody
import java.io.File
import java.util.concurrent.ConcurrentHashMap

abstract class DownLoadManagerImpl<T : BasicDownLoadRequest> {

    private val interceptors by lazy {
        addInterceptor().apply {
            this.add(ParameterInterceptor())
        }
    }

    //保存当前正在执行的任务
    private val map: ConcurrentHashMap<String, T> = ConcurrentHashMap()

    //添加拦截器
    abstract fun addInterceptor(): MutableSet<OnDownLoadInterceptor>

    //开始执行请求
    abstract fun startRequest(
        request: T,
        currentLength: Long,
        block: (ResponseBody?) -> Unit
    ): Disposable

    //判断是否停止保存到文件
    abstract fun stopSave(request: T): Boolean


    //获取Key
    private fun getKey(request: DownLoadRequest): String {
        return request.url
    }

     fun startDownLoad(request: T) {
        MoyaLogTool.i("准备开始校验是否合法")
        if (!check(request)) {
            MoyaLogTool.i("校验不通过")
            return
        }
        MoyaLogTool.i("校验通过")
        //准备下载
//        prepare(request)
        map[getKey(request)] = request
        val file = File("${request.path}${File.separator}${request.name}")
        val currentLength = if (!file.exists()) {
            0L
        } else {
            PrefTools.getLong(request)
        }
        MoyaLogTool.i("当前下载的位置:${currentLength}")
        try {
            val disposable = startRequest(request, currentLength) {
                if (it == null) {
                    //error(request, "response body is null")
                    PrefTools.remove(request)
                    return@startRequest
                }

                FileTool.downToFile(currentLength, request = request, responseBody = it,
                    error = {
                        PrefTools.remove(request)
                        map.remove(getKey(request))
//                        error(request, "file path is null create file fail")

                    },
                    success = { path ->
                        PrefTools.remove(request)
                        map.remove(getKey(request))
//                        success(request, path)
                    },
                    progress = { currentProgress, currentSaveLength, fileLength ->
                        //记录已经下载的长度
                        PrefTools.putLong(request, currentSaveLength)
//                        progress(request, currentProgress, currentSaveLength, fileLength)
                    },
                    stop = {
                        stopSave(request)
                    }
                )
            }
        } catch (t: Throwable) {
//            error(request, t)
            PrefTools.remove(request)
        }
    }


    //检查请求是否可以发送
    private fun <T : BasicDownLoadRequest> check(request: T): Boolean {
        MoyaLogTool.i("校验器size:${interceptors.size}")
        //拦截器
        interceptors
            .reversed()
            .forEach {
                if (!it.onIntercept(request)) {
                    return false
                }
            }
        return true
    }


}