package com.kotiln.test.delegate

class User {
    var p : String by Delegate()

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var user = User()
            user.p = "zhangsan"
            var v = user.p
            println(v)
        }
    }
}