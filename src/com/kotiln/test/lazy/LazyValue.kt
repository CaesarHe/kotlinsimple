package com.kotiln.test.lazy

class LazyValue {
    val email : String by lazy {
        println("lazy")
        "keji@gmail.com"
    }

    fun getUserName() {
        println("userName: ${email.substringBefore("@")}")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var value = LazyValue()
            println(value.email)

            value.getUserName()

        }
    }
}

