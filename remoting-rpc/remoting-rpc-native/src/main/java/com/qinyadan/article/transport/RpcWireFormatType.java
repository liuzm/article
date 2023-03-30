package com.qinyadan.article.transport;

import com.qinyadan.article.command.RpcRequest;
import com.qinyadan.article.command.RpcResponse;
import com.taobao.gecko.core.command.CommandFactory;
import com.taobao.gecko.core.command.CommandHeader;
import com.taobao.gecko.core.command.ResponseStatus;
import com.taobao.gecko.core.command.kernel.BooleanAckCommand;
import com.taobao.gecko.core.command.kernel.HeartBeatRequestCommand;
import com.taobao.gecko.core.core.CodecFactory;
import com.taobao.gecko.service.config.WireFormatType;


public class RpcWireFormatType extends WireFormatType {

    public static final class RpcHeartBeatCommand implements HeartBeatRequestCommand {
        public RpcRequest request = new RpcRequest("heartBeat" + System.currentTimeMillis(), "heartBeat"
                + System.currentTimeMillis(), null);


        public CommandHeader getRequestHeader() {
            return this.request;
        }


        public Integer getOpaque() {
            return this.request.getOpaque();
        }
    }


    @Override
    public String getScheme() {
        return "rpc";
    }


    @Override
    public String name() {
        return "Notify Remoting rpc";
    }


    @Override
    public CodecFactory newCodecFactory() {
        return new RpcCodecFactory();
    }


    @Override
    public CommandFactory newCommandFactory() {
        return new CommandFactory() {

            public HeartBeatRequestCommand createHeartBeatCommand() {
                return new RpcHeartBeatCommand();
            }


            public BooleanAckCommand createBooleanAckCommand(final CommandHeader request,
                    final ResponseStatus responseStatus, final String errorMsg) {
                final BooleanAckCommand ack = new RpcResponse(request.getOpaque(), responseStatus, null) {

                    @Override
                    public boolean isBoolean() {
                        return true;
                    }

                };
                ack.setErrorMsg(errorMsg);
                return ack;
            }
        };
    }

}
