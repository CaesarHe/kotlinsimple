package com.kotiln.test.withlet

data class Test(val name: String, val age: Int, val flag: Boolean = false) {
}

fun main(args: Array<String>) {
    val t = Test("tom", 10)
    val (name, age, flag) = t
    println(name)

    println("-------with------")
    with(t) {
        println("$name-$age-$flag")
        var d = name
        println(d)
    }

    println("-------let------")
    t?.let {
        println(it.name)
        println(it.age)
        println(it.flag)
    }
    println("-------let2------")
    t.let { (name, age) ->
        println(name)
        println(age)
        println(flag)
    }
    println("-------let3------")
    t.let {
        println(name)
    }

    fun Test.getResult(a:Int, b:Int) = a+b
    var result = t.getResult(10, 2)
    println(result)
}