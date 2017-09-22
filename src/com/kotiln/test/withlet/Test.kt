package com.kotiln.test.withlet

data class Test(var name: String, var age: Int, var flag: Boolean = false) {
}

fun main(args: Array<String>) {
    val t = Test("tom", 10)
    println(t)
    println("-------with------")
   var n = with(t) {
        name = "with modify"
        "返回最后一行"
    }
    println("with $t")
    println("with return $n")

    println("-------let------")

    var b = t.let {
        it.name = "let modify"
        "first line"
        "line2"
        "返回最后一行"
    }
    println("let： $t")
    println("let return： $b")
    println("-------apply------")
    var c = t.apply {
        name = "apply modify"
        age = 100
        "最后一行"
    }
    println("apply: $t")
    println("apply return: $c")

    println("-------run------")

    var d = t.run {
        name = "run modify"
        "123"
        "返回最后一行"
    }
    println("run: $t")
    println("run return: $d")

}