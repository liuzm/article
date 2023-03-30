package com.qinyadan.article;


import com.qinyadan.article.server.BeanLocator;
import com.qinyadan.article.server.RpcServer;
import java.net.InetSocketAddress;


public class HelloServer {
    public static void main(String[] args) throws Exception {
        RpcServer rpcServer = new RpcServer();
        rpcServer.bind(new BeanLocator() {

            public Object getBean(String name) {
                if (name.equals("hello")) {
                    return new HelloImpl();
                }
                return null;
            }
        }, new InetSocketAddress(8080));
    }
}
