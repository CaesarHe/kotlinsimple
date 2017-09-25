package com.kotiln.test.enum

enum class RGB(val value: Int) {RED(0), GREEN(1), BLUE(2) }

inline fun <reified T : Enum<T>> printAllValues() {
    println(enumValues<T>().joinToString { it.name })
}

fun main(args: Array<String>) {
    var rgb = RGB.BLUE
    println(rgb.ordinal)
    printAllValues<RGB>()
}