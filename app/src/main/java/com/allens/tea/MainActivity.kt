package com.allens.tea

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.setPadding
import androidx.lifecycle.viewModelScope
import com.allens.moya.Moya
import com.allens.moya_coroutines.request.doGet
import com.allens.moya_coroutines.request.doPost
import com.allens.tea.databinding.ActivityMainBinding
import com.dylanc.viewbinding.binding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by binding()

    private val viewModel: MainViewModel by viewModels()

    private val moya by lazy {
        Moya.Builder()
            // base url
            .baseUrl("https://www.wanandroid.com")
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
            viewModel.viewModelScope.launch {
                moya.create()
                    //一定需要添加一个 lifecycle 或者 viewModel
                    .viewModel(viewModel)
                    .parameter("username", "moya")
                    .parameter("password", "123456")
                    .parameter("repassword", "123456")
                    .doPost<RegisterData>("user/register")
                    .doComplete { hideLoadDialog() }
                    .doFailed { showRequestDialog(it.message ?: " is empty ") }
                    .doSuccess { showRequestDialog(it.toString()) }
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