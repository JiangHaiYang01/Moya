package com.allens.moya.impl

import androidx.annotation.MainThread
import com.allens.moya.interceptor.ParameterInterceptor
import com.allens.moya.livedata.DownLoadStatusLiveData
import com.allens.moya.request.BasicDownLoadRequest
import com.allens.moya.request.DownLoadRequest
import com.allens.moya.result.Disposable
import com.allens.moya.result.DownLoadData
import com.allens.moya.result.DownLoadResult
import com.allens.moya.tools.FileTool
import com.allens.moya.tools.MoyaLogTool
import com.allens.moya.tools.PrefTools
import okhttp3.ResponseBody
import java.io.File
import java.util.concurrent.ConcurrentHashMap

abstract class DownLoadManagerImpl<T : BasicDownLoadRequest, R : Disposable> {

    private val interceptors by lazy {
        addInterceptor().apply {
            this.add(ParameterInterceptor())
        }
    }


    //保存当前正在执行的任务
    private val map: ConcurrentHashMap<String, DownLoadData<R>> = ConcurrentHashMap()

    //添加拦截器
    abstract fun addInterceptor(): MutableSet<OnDownLoadInterceptor>

    //开始执行请求
    abstract fun startRequest(
        request: T,
        currentLength: Long,
        block: (ResponseBody?) -> Unit
    ): R


    //获取Key
    private fun getKey(request: DownLoadRequest): String {
        return request.tag ?: request.url
    }

    //暂停下载
    fun pause(request: T) {
        cancelOrPause(request, DownLoadResult.Pause)
    }

    fun pauseAll() {
        cancelOrPauseAll(DownLoadResult.Pause)
    }

    fun cancelAll() {
        cancelOrPauseAll(DownLoadResult.Cancel)
    }


    //取消下载
    fun cancel(request: T) {
        PrefTools.remove(request)
        cancelOrPause(request, DownLoadResult.Cancel)
    }

    private fun cancelOrPause(request: T, status: DownLoadResult<String>) {
        val downLoadData = map[getKey(request)]
        if (downLoadData != null) {
            val liveData = downLoadData.liveData
            if (liveData != null) {
                changeStatus(liveData, status, request)
                downLoadData.disposable?.dispose()
            }
        }
    }

    @MainThread
    private fun cancelOrPauseAll(status: DownLoadResult<String>) {
        map.forEach {
            it.value.liveData?.value = status
            it.value.disposable?.dispose()
        }
    }


    //修改状态
    private fun <R : Any> changeStatus(
        liveData: DownLoadStatusLiveData<R>,
        status: DownLoadResult<R>,
        request: T
    ) {
        //必须要用 setValue postValue 可能会将事件丢失
        request.manager.handler.post {
            liveData.value = status
        }
    }

    fun startDownLoad(request: T): DownLoadData<R> {
        MoyaLogTool.i("准备开始校验是否合法")
        val liveData = DownLoadStatusLiveData<String>()
        val result = DownLoadData<R>()
        if (!check(request) { changeStatus(liveData, DownLoadResult.Error(it), request) }) {
            MoyaLogTool.i("校验不通过")
            return result.apply {
                this.liveData = liveData
            }
        }
        MoyaLogTool.i("校验通过")
        MoyaLogTool.i("当前线程---->${Thread.currentThread().name}")
        //准备下载
        changeStatus(liveData, DownLoadResult.Prepare, request)
        map[getKey(request)] = result
        val file = File("${request.path}${File.separator}${request.name}")
        val currentLength = if (!file.exists()) {
            0L
        } else {
            PrefTools.getLong(request)
        }
        MoyaLogTool.i("当前下载的位置:${currentLength}")
        var disposable: R? = null
        try {
            disposable = startRequest(request, currentLength) {
                if (it == null) {
                    changeStatus(
                        liveData,
                        DownLoadResult.Error(Throwable("response body is null")),
                        request
                    )
                    PrefTools.remove(request)
                    return@startRequest
                }

                FileTool.downToFile(currentLength, request = request, responseBody = it,
                    error = {
                        PrefTools.remove(request)
                        map.remove(getKey(request))
                        changeStatus(
                            liveData,
                            DownLoadResult.Error(Throwable("create DownLoad file error")),
                            request
                        )
                    },
                    success = { path ->
                        PrefTools.remove(request)
                        map.remove(getKey(request))
                        changeStatus(liveData, DownLoadResult.Success(path), request)

                    },
                    progress = { currentProgress, currentSaveLength, fileLength ->
                        //记录已经下载的长度
                        PrefTools.putLong(request, currentSaveLength)
                        changeStatus(
                            liveData,
                            DownLoadResult.Progress(
                                currentProgress,
                                currentSaveLength,
                                fileLength,
                                done = currentSaveLength == fileLength
                            ),
                            request
                        )
                    },
                    stop = {
                        //判断是否停止保存到文件
                        disposable?.isDisposed ?: false
                    }
                )
            }
        } catch (t: Throwable) {
            MoyaLogTool.i("download error ${t.message}")
            changeStatus(liveData, DownLoadResult.Error(t), request)
            PrefTools.remove(request)
        } finally {
            MoyaLogTool.i("返回 disposable")
            return result.apply {
                this.liveData = liveData
                this.disposable = disposable
            }
        }
    }


    //检查请求是否可以发送
    private fun <T : BasicDownLoadRequest> check(
        request: T,
        error: (Throwable) -> Unit
    ): Boolean {
        MoyaLogTool.i("校验器size:${interceptors.size}")
        //拦截器
        interceptors
            .reversed()
            .forEach {
                if (!it.onIntercept(request) { error -> error(error) }) {
                    return false
                }
            }
        return true
    }


}