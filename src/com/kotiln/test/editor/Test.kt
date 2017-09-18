package com.kotiln.test.editor

fun main(args: Array<String>) {
    fun SharedPreferences.editor(f: (SharedPreferences.Editor) -> Any) {
        val editor = edit()
        f(editor)
        editor.apply()
    }

    var sp = SharedPreferences()
    sp.editor {
        it.putString("name", "zhangsan")
    }
    with(sp.edit()) {
        putString("age", "10")
        apply()
    }


    fun SharedPreferences.run(f:(User) -> Any)  {
        var user = User()
        f(user)
        user.method2()
    }

    sp.run {
        it.method1()
    }
}