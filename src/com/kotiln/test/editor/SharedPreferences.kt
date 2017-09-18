package com.kotiln.test.editor

class SharedPreferences {

    fun edit(): Editor {
        return Editor()
    }

    class Editor {
        fun apply() {
            println("apply")
        }
        fun putString(key: String, value: String?) {
            println("$key, $value")
        }
    }
}