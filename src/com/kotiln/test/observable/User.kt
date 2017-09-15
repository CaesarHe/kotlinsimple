package com.kotiln.test.observable

import kotlin.properties.Delegates

class User {
    var name : String by Delegates.observable("<no-name>") {
        prop, old, new ->
        println("$old -> $new")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var user = User()
            user.name = "keji"

        }
    }
}