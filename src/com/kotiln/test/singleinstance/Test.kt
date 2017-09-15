package com.kotiln.test.singleinstance

class HttpManager {
    private var flag: String? = null

    constructor() {
        flag = "123"
    }

    companion object {
         fun instance() = Holder.instance
    }

    private object Holder {
        val instance = HttpManager()
    }

    fun test() {
        println("run!$flag")
    }

}

fun main(args: Array<String>) {
    var a = HttpManager.instance()
    a.test()
}