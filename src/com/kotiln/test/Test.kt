package com.kotiln.test

import com.kotiln.test.entity.DataObject
import com.kotiln.test.entity.People
import com.kotiln.test.entity.User
import com.kotiln.test.inline.logi

fun main(args: Array<String>) {
    println(Constants.getHttpsHost())
    var user = User("mick")
    println(user.testIf("1"))

    user.doWork("name", 10, desc = "desc")



    println("---------------")
    var people = People("zhangsan", 19)

    println(people.age)

    var dataObject = DataObject("tom", 10)

    println(dataObject.toString())


    logi<Thread>("lalala")
}
