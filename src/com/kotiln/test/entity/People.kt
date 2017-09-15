package com.kotiln.test.entity

class People (val name: String, var age:Int ? = 10) {
    init {
        println("init:" + name + age)
    }
}