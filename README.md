# MOya

## 配置

使用 Moya.Builder()构造器创建一个moya对象

```
private val moya by lazy {
        Moya.Builder()
            // base url
            .baseUrl("https://www.wanandroid.com")
            // 读写连接超时
            .connectTimeout(10)
            .readTimeout(10)
            .writeTimeout(10)
            // 缓存配置
            .cacheType(CacheType.NONE)
            .cachePath("")
            .cacheSize(10)
                
            .cacheNetWorkTimeOut(10)
            .cacheNoNetWorkTimeOut(10)
            // 日志配置
            .logLevel(LoggerLevel.BASIC)
            .debug(true)
            .logInterceptor(object : OnLogInterceptor {
                override fun onLogInterceptorInfo(message: String) {

                }
            })
            // 构建工厂配置
//            .callAdapterFactory()
//            .converterFactory()
            // 配置全部请求头
            .head("hello", "world")
            // 构建moya
            .build(this)
    }
```

或者你可以使用 dsl 去创建一个,推荐使用这种方式。更佳清晰

```
private val moya by lazy {
    moya(this) {
        url = "https://www.wanandroid.com"
        retry = true
        time = {
            connect = 1L
            write = 2L
            read = 3L
        }
        cache = {
            type = CacheType.NONE
            path = ""
            size = 10
            noNetworkTimeOut = 10
            networkTimeOut = 10
        }
        head = {
            put("hello", "world")
        }
        log = {
            level = LoggerLevel.BASIC
            interceptors = setOf(object : OnLogInterceptor {
                override fun onLogInterceptorInfo(message: String) {

                }
            })
        }

        adapter = {}
        converter = {}
        cookie = {}
    }
  }
```

## 请求

常规的请求,下面模拟一个Get 请求

```
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
```

与协程一起使用

```
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
```

### 下载

使用`DownLoadRequest.Builder()` 创建一个下载对象即可。

> 注意

如果在下载中绑定了 lifeCycle 则当绑定的Activity 或者Fragment 进入后天 下载状态不会执行。当进入前台以后才会将状态刷新。
如果没有绑定。则需要在合适的地方。例如Activity 的 onDestroy 方法中执行 `CoroutinesDownLoadManager.removeObserver()` 

```
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
```

### 上传


```
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
```

## 下载使用

Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```
	dependencies {
	        implementation 'com.github.JiangHaiYang01:Moya:Tag'
	}
```

[![](https://www.jitpack.io/v/JiangHaiYang01/Moya.svg)](https://www.jitpack.io/#JiangHaiYang01/Moya)