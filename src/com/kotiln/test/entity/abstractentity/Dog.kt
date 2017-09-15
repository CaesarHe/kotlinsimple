package com.kotiln.test.entity.abstractentity

class Dog(name: String, age: Int, flag: Boolean) : Animal(name, age) {
    init {
        println("Dog init flag:" + flag)
    }

    override fun work() {
        println("Dog: work")
    }
}
