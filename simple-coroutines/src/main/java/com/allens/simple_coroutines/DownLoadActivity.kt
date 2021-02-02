package com.allens.simple_coroutines

import android.telephony.mbms.DownloadRequest
import com.allens.moya.request.DownLoadRequest
import com.allens.moya_coroutines.request.doDownLoad
import kotlinx.coroutines.launch

class DownLoadActivity : BaseActivity() {
    override fun doCreate() {
        addButton("下砸") {
            launch {
                moya.create()
                    .lifecycle(lifecycle = this@DownLoadActivity)
                    .doDownLoad(
                        DownLoadRequest.Builder()
                            .name("aa")
                            .path(getBasePath())
                            .build("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4")
                    )

            }
        }
    }
}