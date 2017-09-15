package com.kotiln.test.lazy

class LazyValue {
    val value : String by lazy {
        println("lazy")
        "zhangsan"
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var value = LazyValue()
            println(value.value)
        }
    }
}

