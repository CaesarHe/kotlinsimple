package com.kotiln.test.manager

import java.util.*

interface ILoginListener {
    fun loginSuccess(name: String, token: String)
    fun loginFailed(name: String)
}

class LoginListenerImp : ILoginListener {
    private var _loginSuccess: ((name: String, token: String) -> Unit)? = null
    override fun loginSuccess(name: String, token: String) {
        _loginSuccess?.invoke(name, token)
    }

    private var _loginFailed: ((name: String) -> Unit)? = null
    override fun loginFailed(name: String) {
        _loginFailed?.invoke(name)
    }

    fun onLoginSuccess(listener: (name: String, token: String) -> Unit) {
        _loginSuccess = listener
    }

    fun onLoginFailed(listener: (name: String) -> Unit) {
        _loginFailed = listener
    }

}

class LoginManager {
    private var listener: ILoginListener? = null

    private constructor()
    companion object {
        private var instance: LoginManager? = null
        @Synchronized
        fun getInstance(name: String = ""): LoginManager {
            if (instance == null) {
                instance = LoginManager()
            }
            return instance!!
        }
    }

    fun addListener(init: (LoginListenerImp.() -> Unit)): LoginManager {
        var imp = LoginListenerImp()
        imp.init()
        this.listener = imp
        return this
    }

    fun login(name: String, pwd: String) {
        var random = Random().nextBoolean()
        if (random)
            listener?.loginSuccess(name, "DSDAFW0SWEFSAFASDF=")
        else
            listener?.loginFailed(name)

    }

}

fun main(args: Array<String>) {
    var loginManager = LoginManager.getInstance()
    loginManager.addListener {
        onLoginFailed { name: String ->
            println("$name loginFailed")
        }

        onLoginSuccess { name, token ->
            println("$name loginSuccess $token")
        }
    }.login("zhangsan", "123456")
}