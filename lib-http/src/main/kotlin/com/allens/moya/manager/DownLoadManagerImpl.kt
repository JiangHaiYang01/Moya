package com.allens.moya.manager

import androidx.annotation.MainThread
import androidx.lifecycle.Observer
import com.allens.moya.impl.OnDownLoadInterceptor
import com.allens.moya.interceptor.ParameterInterceptor
import com.allens.moya.livedata.DownLoadStatusLiveData
import com.allens.moya.request.BasicDownLoadRequest
import com.allens.moya.request.DownLoadRequest
import com.allens.moya.request.getKey
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


    //下载的observer 在传入lifecycle 会自动remove
    //未传入 则需要手动取消
    internal var observer =
        HashMap<String, Pair<DownLoadStatusLiveData, Observer<DownLoadResult>>>()

    //删除未绑定LifeCycle的LiveData
    fun removeObserver() {
        MoyaLogTool.i("删除未绑定LifeCycle的LiveData size ${observer.size}")
        observer.forEach {
            val pair = observer[it.key] ?: return
            MoyaLogTool.i("remove observer ")
            pair.first.removeObserver(pair.second)
        }
        observer.clear()
    }

    //开始执行请求
    abstract fun startRequest(
        request: T,
        currentLength: Long,
        block: (ResponseBody?) -> Unit
    ): R


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

    private fun cancelOrPause(request: T, status: DownLoadResult) {
        val downLoadData = map[request.getKey()]
        if (downLoadData != null) {
            val liveData = downLoadData.liveData
            if (liveData != null) {
                //一定要线 cancel  在更新状态 否在在 afterStopSave 获取的状态将不对
                downLoadData.disposable?.dispose()
                changeStatus(liveData, status, request)
            }
        }
    }

    @MainThread
    private fun cancelOrPauseAll(status: DownLoadResult) {
        map.forEach {
            it.value.disposable?.dispose()
            it.value.liveData?.value = status
        }
    }


    //修改状态
    private fun changeStatus(
        liveData: DownLoadStatusLiveData,
        status: DownLoadResult,
        request: T
    ) {
        //必须要用 setValue postValue 可能会将事件丢失
        request.manager.handler.post {
            liveData.value = status
        }
    }

    fun startDownLoad(request: T): DownLoadData<R> {
        MoyaLogTool.i("准备开始校验是否合法")
        val liveData = DownLoadStatusLiveData()
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
        map[request.getKey()] = result
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
                    error(request, liveData, "response body is null")
                    return@startRequest
                }
                FileTool.downToFile(
                    currentLength = currentLength,
                    request = request,
                    responseBody = it,
                    error = {
                        error(request, liveData, "create DownLoad file error")
                    },
                    success = { path ->
                        success(request, liveData, path)
                    },
                    progress = { currentProgress, currentSaveLength, fileLength ->
                        progress(request, currentSaveLength, liveData, currentProgress, fileLength)
                    },
                    stop = {
                        //判断是否停止保存到文件
                        disposable?.isDisposed ?: false
                    },
                    afterStopSave = {
                        afterStopSave(request)
                    }
                )
            }
        } catch (t: Throwable) {
            MoyaLogTool.i("download error ${t.message}")
            changeStatus(liveData, DownLoadResult.Error(t), request)
            PrefTools.remove(request)
        }
        MoyaLogTool.i("返回 disposable")
        return result.apply {
            this.liveData = liveData
            this.disposable = disposable
        }
    }

    //这里记录了在外部被打断执行以后的状态。
    private fun afterStopSave(request: T) {
        MoyaLogTool.i("stop save")
        //判断当前的任务状态 如果正在执行 说明是在外部被取消的 状态改成cancel
        val value = map[request.getKey()]?.liveData?.value ?: return
        MoyaLogTool.i("当前的状态 ${value::class.java}")
        if (value::class.java != DownLoadResult.Cancel::class.java) {
            MoyaLogTool.i("Disable is cancel status is not cancel")
        }
    }

    //记录已经下载的长度,修改状态。
    private fun progress(
        request: T,
        currentSaveLength: Long,
        liveData: DownLoadStatusLiveData,
        currentProgress: Int,
        fileLength: Long
    ) {
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
    }

    //文件下载成功
    private fun success(
        request: T,
        liveData: DownLoadStatusLiveData,
        path: String
    ) {
        PrefTools.remove(request)
        map.remove(request.getKey())
        changeStatus(liveData, DownLoadResult.Success(path), request)
    }

    private fun error(request: T, liveData: DownLoadStatusLiveData, errorInfo: String) {
        PrefTools.remove(request)
        map.remove(request.getKey())
        changeStatus(
            liveData,
            DownLoadResult.Error(Throwable(errorInfo)),
            request
        )
    }


    //检查请求是否可以发送
    private fun <T : BasicDownLoadRequest> check(
        request: T,
        error: (Throwable) -> Unit
    ): Boolean {
        MoyaLogTool.i("校验器size:${interceptors.size}")
        //拦截器
        interceptors
            //使用倒叙。先执行moya 提供的基础拦截器 ParameterInterceptor
            .reversed()
            .forEach {
                if (!it.onIntercept(request) { error -> error(error) }) {
                    return false
                }
            }
        return true
    }


}