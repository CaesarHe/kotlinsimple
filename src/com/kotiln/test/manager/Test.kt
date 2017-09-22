package com.kotiln.test.manager

open class RequestWrapper {
    var url: String = ""
    //default 1
    var method: Int = 1
    private var _start: ((String) -> Unit) = {}
    private var _progress: ((Int) -> Unit) = {}
    fun onStart(onStart: (String) -> Unit) {
        _start = onStart
    }

    fun onProgress(onProgress: ((Int) -> Unit)) {
        _progress = onProgress
    }

    fun execute() {
        println("execute $url")
        _start(url)
        _progress(1)
        _progress(100)
    }
}

object Http {
    private val request: (method: Int, RequestWrapper.() -> Unit) -> Unit = { method, init ->
        val baseRequest = RequestWrapper()

        baseRequest.method = method
        baseRequest.init()
        baseRequest.execute()
    }

    fun get(block: RequestWrapper.() -> Unit) {
        request(2) {
            block()
        }
    }
}

fun main(args: Array<String>) {
    Http.get {
        url = "http://www.google.com"
        onStart { url ->
            println("onstart $url")
        }
        onProgress { progress ->
            println("progress $progress")
        }
    }

}