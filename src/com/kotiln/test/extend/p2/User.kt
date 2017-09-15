package com.kotiln.test.extend.p2

open class User {
    var name: String? = null
    var age: Int = 0

    constructor(name: String, age: Int) {
        this.name = name
        this.age = age
    }
}

fun main(args: Array<String>) {
    for(i in 0..10 step 2) {
        println(i)
    }
}