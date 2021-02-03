package com.allens.simple_coroutines

import android.util.Log
import com.allens.moya.impl.OnDownLoadListener
import com.allens.moya.request.DownLoadRequest
import com.allens.moya_coroutines.request.doDownLoad
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DownLoadActivity : BaseActivity(), OnDownLoadListener {
    override fun doCreate() {
        addButton("下载 - 通过监听接口") {
            launch {
                val request = DownLoadRequest.Builder()
                    .name("aa")
                    .path(getBasePath())
                    .tag("key1")
                    .listener(this@DownLoadActivity)
                    .build("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4")
                moya.create()
                    .lifecycle(lifecycle = this@DownLoadActivity)
                    .doDownLoad(request)
                println("继续执行")
            }
        }


        addButton("下载 - lambda") {
            launch {
                val request = DownLoadRequest.Builder()
                    .name("bb")
                    .path(getBasePath())
                    .build("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4")
                val disposable = moya.create()
                    .lifecycle(lifecycle = this@DownLoadActivity)
                    .doDownLoad(request) {
                        onSuccess = { println("下载成功 保存在 $it") }
                        onError = { println("下载失败 ${it.message}") }
                        onCancel = { println("取消") }
                        onPause = { println("暂停") }
                        onPrepare = { println("准备下载") }
                        onProgress = { println("进度 $it") }
                    }
            }
        }
    }


    override fun onDownLoadPrepare(key: String) {
        println("准备下载")
    }

    override fun onDownLoadProgress(key: String, progress: Int) {
        println("进度 $progress")
    }

    override fun onDownLoadError(key: String, throwable: Throwable) {
        println("下载失败 ${throwable.message}")
    }

    override fun onDownLoadSuccess(key: String, path: String) {
        println("下载成功 保存在 $path")
    }

    override fun onDownLoadPause(key: String) {
        println("暂停")
    }

    override fun onDownLoadCancel(key: String) {
        println("取消")
    }

    private fun println(info: String) {
        Log.i("tag", "$info in ${Thread.currentThread().name}")
    }


}