package com.allens.simple_coroutines

import android.content.Intent
import android.os.Build

class MainActivity : BaseActivity() {

    override fun doCreate() {
        addButton("常规使用") {
            startActivity(Intent(this, TestActivity::class.java))
        }
        addButton("下载") {
            startActivity(Intent(this, DownLoadActivity::class.java))
        }
        addButton("上传") {
            startActivity(Intent(this, UploadAct::class.java))
        }
    }
}