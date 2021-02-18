package com.allens.simple_coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.allens.moya.Moya
import com.allens.moya_coroutines.request.doGetBlock
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import java.io.File

abstract class BaseActivity(private var layout: Int = R.layout.activity_main) :
    AppCompatActivity(),
    CoroutineScope by MainScope() {
    lateinit var linear: LinearLayout

    lateinit var moya: Moya

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        linear = findViewById(R.id.linear)
        moya = Moya.Builder()
            .baseUrl("https://www.wanandroid.com")
            .build(this)
        doCreate()
    }

    abstract fun doCreate()

    fun addButton(info: String, action: () -> Unit) {
        linear.addView(createButton(info, action))
    }

    private fun createButton(info: String, action: () -> Unit): MaterialButton {
        return MaterialButton(this).apply {
            text = info
            setOnClickListener {
                action()
            }
        }
    }

    //创建ViewModel
    fun <VM : ViewModel> createViewModel(
        owner: ViewModelStoreOwner, cls: Class<VM>
    ): VM {
        return ViewModelProvider(owner).get(cls)
    }

    //获取更路径
    fun getBasePath(): String {
        var p: String = Environment.getExternalStorageState()
        val f: File? = getExternalFilesDir(null)
        if (null != f) {
            p = f.absolutePath
        }
        return p
    }

    fun toast(info: String) {
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
    }
}

class MainViewModel : ViewModel(), LifecycleObserver {
    fun doGet(moya: Moya) {
        moya.create()
            .parameter("k", "java")
            .viewModel(this)
            .doGetBlock<String>("wxarticle/chapters/json") {
                it.doSuccess {
                    log("success")
                }
                it.doFailed {
                    log("error")
                }
                it.doComplete {
                    log("complete")
                }
            }
    }
}

fun log(info: String) {
    val stringBuffer = StringBuffer()
    stringBuffer.append(info)
        .append(" ")
        .append(Thread.currentThread().name)
    Log.e("log--->", stringBuffer.toString())
}
