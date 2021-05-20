package com.allens.moya.message

import androidx.annotation.experimental.Experimental

class MoyaMessage {

    companion object {
        private const val METHOD = "(\n" +
                "    parameter: String,\n" +
                "    crossinline init: HttpBuilder<T>.() -> Unit\n" +
                ")"

        const val IMPORTS = "Request.Builder"
        const val Deprecated = "The use of lambda in this method is not elegant."
        private const val EXPRESSION = "It is recommended to use within the coroutine \n"
        const val GET = EXPRESSION + "doGet" + METHOD
        const val POST = EXPRESSION + "doPost" + METHOD
        const val DELETE = EXPRESSION + "doDelete" + METHOD
        const val BODY = EXPRESSION + "doBody" + METHOD
        const val PUT = EXPRESSION + "doPut" + METHOD
        const val doUpLoad = EXPRESSION + "doUpLoad" + METHOD


    }
}