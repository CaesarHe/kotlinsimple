package com.kotiln.test.manager

class RegisterManager {

    companion object {
        private var instance: RegisterManager? = null

        @Synchronized
        fun getInstance(name: String = ""): RegisterManager {
            if (instance == null) instance = RegisterManager()
            return instance!!
        }
    }

    fun regist(name: String, pwd: String) {
        registerListener?.success("${name + pwd}safdfsafdas===")
    }

    private var registerListener: RegisterListener? = null
    fun addListener(init: (RegisterListener.() -> Unit)): RegisterManager {
        var listener = RegisterListener()
        listener.init()
        this.registerListener = listener
        return this
    }

    class RegisterListener {
        private var failedListener: ((error: String) -> Unit)? = null
        fun onFailed(listener: (error: String) -> Unit) {
            failedListener = listener
        }

        fun failed(error: String) {
            failedListener?.invoke(error)
        }

        fun success(token: String) {
            successListener?.invoke(token)
        }

        private var successListener: ((token: String) -> Unit)? = null
        fun onSuccess(listener: (token: String) -> Unit) {
            successListener = listener
        }
    }
}

fun main(args: Array<String>) {
    RegisterManager.getInstance().addListener {
        onFailed { error ->
            println(error)
        }

        onSuccess { token ->
            println(token)
        }
    }.regist("zhangsan", "123")
}