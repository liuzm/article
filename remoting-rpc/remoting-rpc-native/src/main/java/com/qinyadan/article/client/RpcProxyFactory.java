package com.qinyadan.article.client;

import com.qinyadan.article.command.RpcRequest;
import com.qinyadan.article.command.RpcResponse;
import com.qinyadan.article.exception.RpcRuntimeException;
import com.qinyadan.article.transport.RpcWireFormatType;
import com.taobao.gecko.core.command.ResponseStatus;
import com.taobao.gecko.service.RemotingClient;
import com.taobao.gecko.service.RemotingFactory;
import com.taobao.gecko.service.config.ClientConfig;
import com.taobao.gecko.service.exception.NotifyRemotingException;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class RpcProxyFactory {
    private final RemotingClient remotingClient;


    public RpcProxyFactory() throws IOException {
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.setWireFormatType(new RpcWireFormatType());
        this.remotingClient = RemotingFactory.newRemotingClient(clientConfig);
        try {
            this.remotingClient.start();
        } catch (NotifyRemotingException e) {
            throw new IOException(e);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T proxyRemote(final String uri, final String beanName, Class<T> serviceClass) throws IOException,
            InterruptedException {
        try {
            this.remotingClient.connect(uri);
            this.remotingClient.awaitReadyInterrupt(uri);
        } catch (NotifyRemotingException e) {
            throw new IOException(e);
        }

        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {serviceClass}, new InvocationHandler() {

                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequest request = new RpcRequest(beanName, method.getName(), args);
                        RpcResponse response = null;
                        try {
                            response = (RpcResponse) RpcProxyFactory.this.remotingClient.invokeToGroup(uri, request);
                        } catch (Exception e) {
                            throw new RpcRuntimeException("Rpc failure", e);
                        }
                        if (response == null) {
                            throw new RpcRuntimeException("Rpc failure,no response from rpc server");
                        }
                        if (response.getResponseStatus() == ResponseStatus.NO_ERROR) {
                            return response.getResult();
                        } else {
                            throw new RpcRuntimeException("Rpc failure:" + response.getErrorMsg());
                        }
                    }
                });

    }
}
