package com.allens.tea

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.setPadding
import androidx.lifecycle.viewModelScope
import com.allens.moya.*
import com.allens.moya.request.DownLoadRequest
import com.allens.moya_coroutines.request.*
import com.allens.tea.databinding.ActivityMainBinding
import com.dylanc.viewbinding.binding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by binding()

    private val viewModel: MainViewModel by viewModels()

    companion object {
        const val TAG = "moya"
    }

    private val moya by lazy {
        Moya.Builder()
            // base url
            .baseUrl("https://www.wanandroid.com")
            .head("hello", "world")
            .build(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addButton("get请求 - lifecycle") {
            showLoadingDialog()
            moya.create()
                //一定需要添加一个 lifecycle 或者 viewModel
                .lifecycle(this)
                .parameter("k", "java")
                //这里泛形可以直接使用对象。
                .doGet<String>("wxarticle/chapters/json") {
                    onSuccess = { showRequestDialog(it) }
                    onError = { showRequestDialog(it.message ?: " is empty ") }
                    onComplete = { hideLoadDialog() }
                }
        }

        addButton("post请求 - viewModel") {
            showLoadingDialog()
            moya.create()
                //一定需要添加一个 lifecycle 或者 viewModel
                .viewModel(viewModel)
                .parameter("username", "moya")
                .parameter("password", "123456")
                .parameter("repassword", "123456")
                .lifecycle(this)
                .doPost<String>("user/register") {
                    onSuccess = { showRequestDialog(it) }
                    onError = { showRequestDialog(it.message ?: " is empty ") }
                    onComplete = { hideLoadDialog() }
                }
        }


        addButton("post请求 - viewModel - 协程") {
            showLoadingDialog()
            // 笔者这里使用的是 viewModelScope 实际情况实际使用
            // 使用了协程 就没不要一定带上 viewModel 了 协程就需要自行控制了。
            // 实际项目中建议使用ViewModel 的协程。方便资源回收与暂停
            viewModel.viewModelScope.launch {
                moya.create()
                    .parameter("username", "moya")
                    .parameter("password", "123456")
                    .parameter("repassword", "123456")
                    .doPost<RegisterData>("user/register")
                    .doComplete { hideLoadDialog() }
                    .doFailed { showRequestDialog(it.message ?: " is empty ") }
                    .doSuccess { showRequestDialog(it.toString()) }
            }
        }

        addButton("下载 - 查看logcat") {
            // 支持断点下载 暂停等操作。

            val request = DownLoadRequest.Builder()
                .tag("tag-1")
                .name("1.jpg")
                .path(cacheDir.path + File.separator + "download")
                .build("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1714860920,1362844517&fm=26&gp=0.jpg")

            // 暂停操作执行
            // moya.create().doPauseDownLoad(request)

            // 取消操作执行
            // moya.create().doCancelDownLoad(request)

            viewModel.viewModelScope.launch {
                moya.create()
                    // 如果传入了 lifecycle 就交给lifecycle控制，缺点是在后台的时候，不会在change变化
                    // 如果没有绑定lifecycle 则需要自己去执行下面的代码去removeObserver
                    // CoroutinesDownLoadManager.removeObserver()

                    //根于实际情况处理
                    .lifecycle(this@MainActivity)

                    .doDownLoad(
                        request
                    ) {
                        onSuccess = { tag, path ->
                            Log.i(TAG, "onSuccess:$tag path:$path")
                        }
                        onCancel = {
                            Log.i(TAG, "onCancel:$it")
                        }
                        onPause = {
                            Log.i(TAG, "onPrepare:$it")
                        }
                        onProgress = { tag, progress ->
                            Log.i(TAG, "onProgress:$tag progress:$progress")
                        }
                        onUpdate = { tag: String,
                                     progress: Int,
                                     read: Long,
                                     count: Long,
                                     done: Boolean ->
                            Log.i(
                                TAG,
                                "onUpdate:tag:$tag progress:$progress,read:$read,count:$count,done:$done"
                            )
                        }
                        onError = { tag, throwable ->
                            Log.i(TAG, "onError tag:$tag info:${throwable.message}")
                        }
                        onPrepare = {
                            Log.i(TAG, "onPrepare tag:$it ")
                        }
                    }
            }
        }


        addButton("上传") {
            viewModel.viewModelScope.launch {
                moya.create()
                    .file(
                        "file",
                        File(cacheDir.path + File.separator + "download" + File.separator + "1.jpg")
                    )
                    .baseUrl("https://imgkr.com/")
                    .heard("Referer", "https://imgkr.com/")
                    .doUpLoad<String>("api/v2/files/upload") {
                        onSuccess = { Log.i(TAG, "onSuccess :$it ") }
                        onError = { Log.i(TAG, "onError :$it ") }
                        onProgress = { progress: Int, current: Long, length: Long ->
                            Log.i(
                                TAG,
                                "onProgress progress:$progress，current:$current,length:$length "
                            )
                        }
                    }
            }

        }
    }


    private val loadingDialog: Dialog by lazy {
        Dialog(this).apply {
            setContentView(TextView(this@MainActivity).apply {
                text = "加载中..."
                setPadding(20)
            })
        }
    }

    private var requestDialog: Dialog? = null


    private fun showRequestDialog(msg: String) {
        requestDialog = Dialog(this).apply {
            setContentView(TextView(this@MainActivity).apply {
                text = msg
                setPadding(20)
            })
        }
        requestDialog?.show()
    }

    private fun showLoadingDialog() {
        requestDialog?.hide()
        if (!loadingDialog.isShowing)
            loadingDialog.show()
    }

    private fun hideLoadDialog() {
        loadingDialog.hide()
    }


    private fun addButton(info: String, action: () -> Unit) {
        binding.linear.addView(createButton(info, action))
    }

    private fun createButton(info: String, action: () -> Unit): MaterialButton {
        return MaterialButton(this).apply {
            text = info
            setOnClickListener {
                action()
            }
        }
    }

}