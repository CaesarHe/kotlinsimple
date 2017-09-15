package com.kotiln.test

object Constants {
    val HOST: String = "www.kotilin.com"
    fun getHttpsHost(): String {
        return HOST.replace("http", "https")
    }
}