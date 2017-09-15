package com.kotiln.test.extend.p2

class UserChild : User {
    var flag: Boolean = false

    constructor(name: String, age: Int, flag: Boolean = true) : super(name, age) {
        this.flag = flag
    }
}


fun main(args: Array<String>) {
    var user = UserChild("zhangsan", 10)
    println(user.flag)
}

class Mchild (name:String, age: Int, flag: Boolean): User(name, age) {

}