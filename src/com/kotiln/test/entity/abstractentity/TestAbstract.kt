package com.kotiln.test.entity.abstractentity


object TestAbstract {
    @JvmStatic
    fun main(args: Array<String>) {
        var dog = Dog("柯基", 10, false)
        if (dog is Animal) {
            dog.work()
        }


    }
}