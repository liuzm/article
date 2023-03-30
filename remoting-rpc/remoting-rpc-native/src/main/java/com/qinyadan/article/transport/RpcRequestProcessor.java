package com.qinyadan.article.transport;

import com.qinyadan.article.command.RpcRequest;
import com.qinyadan.article.command.RpcResponse;
import com.qinyadan.article.server.BeanLocator;
import com.qinyadan.article.server.RpcSkeleton;
import com.taobao.gecko.core.command.ResponseStatus;
import com.taobao.gecko.service.Connection;
import com.taobao.gecko.service.RequestProcessor;
import java.util.concurrent.ThreadPoolExecutor;


public class RpcRequestProcessor implements RequestProcessor<RpcRequest> {
    private final ThreadPoolExecutor executor;
    private final BeanLocator beanLocator;


    public RpcRequestProcessor(ThreadPoolExecutor executor, BeanLocator beanLocator) {
        super();
        this.executor = executor;
        this.beanLocator = beanLocator;
    }


    public ThreadPoolExecutor getExecutor() {
        return this.executor;
    }


    public void handleRequest(RpcRequest request, Connection conn) {
        Object bean = this.beanLocator.getBean(request.getBeanName());
        if (bean == null) {
            throw new RuntimeException("Could not find bean named " + request.getBeanName());
        }
        RpcSkeleton skeleton = new RpcSkeleton(request.getBeanName(), bean);
        Object result = skeleton.invoke(request.getMethodName(), request.getArguments());
        try {
            conn.response(new RpcResponse(request.getOpaque(), ResponseStatus.NO_ERROR, result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
