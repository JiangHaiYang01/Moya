package com.allens.simple_coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.allens.moya.Moya
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    lateinit var linear: LinearLayout

    lateinit var moya: Moya

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

    fun log(info: String) {
        val stringBuffer = StringBuffer()
        stringBuffer.append(info)
            .append(" ")
            .append(Thread.currentThread().name)
        Log.e("log--->", stringBuffer.toString())
    }
}