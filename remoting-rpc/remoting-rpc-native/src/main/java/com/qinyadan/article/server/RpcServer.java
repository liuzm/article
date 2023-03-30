package com.qinyadan.article.server;

import com.qinyadan.article.command.RpcRequest;
import com.qinyadan.article.transport.RpcRequestProcessor;
import com.qinyadan.article.transport.RpcWireFormatType;
import com.taobao.gecko.service.RemotingFactory;
import com.taobao.gecko.service.RemotingServer;
import com.taobao.gecko.service.config.ServerConfig;
import com.taobao.gecko.service.exception.NotifyRemotingException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class RpcServer {
    private InetSocketAddress serverAddr;

    private RemotingServer remotingServer;


    public void bind(BeanLocator beanLocator, InetSocketAddress serverAddr) throws IOException {
        final ServerConfig serverConfig = new ServerConfig();
        serverConfig.setWireFormatType(new RpcWireFormatType());
        this.serverAddr = serverAddr;
        serverConfig.setLocalInetSocketAddress(serverAddr);
        this.remotingServer = RemotingFactory.newRemotingServer(serverConfig);
        this.remotingServer.registerProcessor(RpcRequest.class,
                new RpcRequestProcessor((ThreadPoolExecutor) Executors.newCachedThreadPool(), beanLocator));
        try {
            this.remotingServer.start();
        } catch (NotifyRemotingException e) {
            throw new IOException(e);
        }
    }
}
