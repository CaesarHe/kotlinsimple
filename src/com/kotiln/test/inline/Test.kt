package com.kotiln.test.inline

inline fun <reified T> logi(log: Any): Unit {
    val resultT = "${T::class.simpleName}: $log"
    println(resultT)
}

inline fun Any.print() {
    println(toString())
}

sealed class Expr
data class Const(var number: Double) : Expr()
data class Sum(val e1: Expr, val e2: Expr) : Expr()
object NotANumber : Expr()

fun eval(expr: Expr): Double = when (expr) {
    is Const -> expr.number
    is Sum -> eval(expr.e1) + eval(expr.e2)
    NotANumber -> Double.NaN
}

fun main(args: Array<String>) {
    eval(Const(10.0)).print()

}
