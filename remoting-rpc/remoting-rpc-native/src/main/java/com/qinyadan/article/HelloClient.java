package com.qinyadan.article;


import com.qinyadan.article.client.RpcProxyFactory;

public class HelloClient {
    public static void main(String[] args) throws Exception {
        RpcProxyFactory factory = new RpcProxyFactory();
        Hello hello = factory.proxyRemote("rpc://localhost:8080", "hello", Hello.class);
        System.out.println(hello.sayHello("hello world!", 10000));
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            hello.add(1, 300);
        }
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        HelloImpl helloImpl = new HelloImpl();
        for (int i = 0; i < 10000; i++) {
            helloImpl.add(1, 300);
        }
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(hello.getDate());
    }
}
