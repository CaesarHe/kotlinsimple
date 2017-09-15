package com.kotiln.test.extendmethod

class C {
    var name: String = "name"
    fun foo() { println("member") }

    companion object {
        @JvmStatic
        fun staticMethod() {
            println("static method")
        }
    }
}


fun C.foo(i: Int) { println("extension") }

fun main(args: Array<String>) {

    C().foo(1)
}