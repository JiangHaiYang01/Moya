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


    object Kotlin {
        private const val version = "1.5.0"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val coreKtx = "androidx.core:core-ktx:1.5.0"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"


        object Test {
            object Ext {
                private const val version = "1.1.2"
                const val junit = "androidx.test.ext:junit-ktx:$version"
            }

            const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
        }
    }
}
