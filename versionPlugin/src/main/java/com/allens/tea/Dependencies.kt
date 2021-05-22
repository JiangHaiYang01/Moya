package com.allens.tea

object BuildConfig {
    const val compileSdkVersion = 30
    const val buildToolsVersion = "30.0.3"
    const val minSdkVersion = 21
    const val targetSdkVersion = 30
    const val versionCode = 1
    const val versionName = "1.0"
}

object Libs {

    const val junit = "junit:junit:4.+"
    const val material = "com.google.android.material:material:1.3.0"


    //cookie
    const val persistentCookieJar = "com.github.franmontiel:PersistentCookieJar:v1.0.1"


    object Kotlin {
        private const val version = "1.5.0"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val coreKtx = "androidx.core:core-ktx:1.5.0"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"

        object Activity {
            private const val version = "1.2.3"
            const val activityKtx = "androidx.activity:activity-ktx:$version"
        }

        object Test {
            object Ext {
                private const val version = "1.1.2"
                const val junit = "androidx.test.ext:junit-ktx:$version"
            }

            const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
        }
    }

    object Squareup {
        const val retrofit2 = "com.squareup.retrofit2:retrofit:2.9.0"

        object OkHttp3 {
            const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.8.1"
        }
    }

    object Google {
        const val gson = "com.google.code.gson:gson:2.8.6"
    }

    object Tencent {
        const val mmkv = "com.tencent:mmkv-static:1.2.8"
    }


    object Lifecycle {
        private const val version = "2.3.1"
        const val livedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
        const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
    }

    object Custom {
        const val http = ":moya"
        const val httpCoroutines = ":moya-coroutines"
    }

    object Github {
        const val viewbinding = "com.github.DylanCaiCoding.ViewBindingKTX:viewbinding-ktx:1.2.0"
    }
}
