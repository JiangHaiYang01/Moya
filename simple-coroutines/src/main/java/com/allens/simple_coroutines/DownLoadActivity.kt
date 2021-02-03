package com.allens.simple_coroutines

import com.allens.moya.impl.OnDownLoadListener
import com.allens.moya.request.DownLoadRequest
import com.allens.moya_coroutines.request.doDownLoad
import kotlinx.coroutines.launch

class DownLoadActivity : BaseActivity(), OnDownLoadListener {
    override fun doCreate() {
        addButton("下砸") {
            launch {
                val doDownLoad = moya.create()
                    .lifecycle(lifecycle = this@DownLoadActivity)
                    .doDownLoad(
                        DownLoadRequest.Builder()
                            .name("aa")
                            .path(getBasePath())
//                            .listener(this@DownLoadActivity)
                            .build("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4")
                    )
                println("获取到了 doDownLoad")
                doDownLoad
                    .doSuccess {
                        println("success:$it")
                    }
                    .doFail {
                        println("fail:${it.message}")
                    }
                    .doPrepare {
                        println("prepare")
                    }
                    .doProgress {
                        println("progress:$it")
                    }

            }
        }
    }


    override fun onDownLoadPrepare(key: String) {
        println("onDownLoadPrepare")
    }

    override fun onDownLoadProgress(key: String, progress: Int) {
        println("onDownLoadProgress")
    }

    override fun onDownLoadError(key: String, throwable: Throwable) {
        println("onDownLoadError")
    }

    override fun onDownLoadSuccess(key: String, path: String) {
        println("onDownLoadSuccess")
    }

    override fun onDownLoadPause(key: String) {
        println("onDownLoadPause")
    }

    override fun onDownLoadCancel(key: String) {
        println("onDownLoadCancel")
    }

    override fun onUpdate(key: String, progress: Int, read: Long, count: Long, done: Boolean) {
        println("onUpdate")
    }


}