package com.kotiln.test.extendmethod;

public class Test {
    public static void main(String[] args) {
        C c = new C();
        //扩展方法
        ExtendmethodKt.foo(c, 10);
        c.foo();
        C.staticMethod();
    }
}
