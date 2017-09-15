package com.kotiln.test.entity

class User {
    companion object {
        val HOST: String = "http://www.kotilin.com"
    }

    var name: String = "123"
        get() = field
        set(value) {
            field = value.toUpperCase()
        }
    val desc: String = "desc"

    constructor(name: String) {
        this.name = name
    }

    fun doWork(name: String?, age: Int, flag: Boolean = false, desc: String): Boolean {
        println("dowork:" + name + age + flag + desc)
        return false

    }

    override fun toString(): String {
        return "toString() User(name='$name', desc='$desc')"
    }

    fun testReturn(flag: String) = flag

    fun testd(args: Array<String>) {
        print(args)
    }

    fun testIf(value: String): String {
        var data = if (value.equals("1")) {
            "one"
        } else if (value.equals("2")) {
            "two"
        } else {
            "other"
        }
        return data
    }
}