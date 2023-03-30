package com.qinyadan.article;

import java.util.Date;


public class HelloImpl implements Hello {

    public String sayHello(String name, int age) {
        // throw new RuntimeException("fuck");
        return "hello," + name + ",your age is " + age;

    }


    public int add(int a, int b) {
        return a + b;
    }


    public Date getDate() {
        return new Date();
    }

}
