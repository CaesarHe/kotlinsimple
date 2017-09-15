package com.kotiln.test.url

import java.net.URL

fun main(args: Array<String>) {
    var data = URL("http://www.google.com.hk").readText()
    println(data)
}