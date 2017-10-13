package com.kotiln.test.override

/**
 * 解决重载冲突
 */
class Manager : AListener, BListener {
    override fun method1() {
        super<AListener>.method1()
    }

    override fun method2() {
        super<AListener>.method2()
        super<BListener>.method2()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            var manager = Manager()
            manager.method1()
            manager.method2()
        }
    }

}