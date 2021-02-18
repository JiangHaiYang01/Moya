package com.allens.simple_coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.allens.moya.Moya
import com.allens.moya_coroutines.request.doBody
import com.allens.moya_coroutines.request.doGet
import com.allens.moya_coroutines.request.doGetBlock
import com.allens.simple_coroutines.bean.TestBean
import kotlinx.coroutines.launch

class TestActivity : BaseActivity() {

    override fun doCreate() {
        addButton("Get 依赖外部协程") {
            launch {
                moya.create()
                    .parameter("k", "java")
                    .doGet<String>("wxarticle/chapters/json")
                    .doFailed {
                        log("error : ${it.message}")
                        toast("error ${it.message}")
                    }
                    .doComplete {
                        log("complete")
                    }
                    .doSuccess {
                        log("success")
                        toast("success")
                    }
            }
        }

        addButton("Block方式1") {
            moya.create()
                .parameter("k", "java")
                //必须绑定lifecycle
                .lifecycle(this)
                .doGetBlock<String>("wxarticle/chapters/json") {
                    it.doSuccess {
                        log("success")
                        toast("success")
                    }
                    it.doFailed {
                        log("error ${it.message}")
                        toast("error ${it.message}")
                    }
                    it.doComplete { log("complete") }
                }
        }

        addButton("Get 绑定lifeCycle") {
            moya.create()
                .parameter("k", "java")
                //必须绑定lifecycle
                .lifecycle(this)
                .doGet<String>("wxarticle/chapters/json") {
                    onSuccess = {
                        log("success")
                        toast("success")
                    }
                    onError = {
                        log("error")
                        toast("error ${it.message}")
                    }
                    onComplete = {
                        log("complete")
                    }
                }
        }

        val viewModel = createViewModel(this, MainViewModel::class.java)
        lifecycle.addObserver(viewModel)
        addButton("Get 绑定ViewModel") {
            viewModel.doGet(moya)
        }
    }
}
