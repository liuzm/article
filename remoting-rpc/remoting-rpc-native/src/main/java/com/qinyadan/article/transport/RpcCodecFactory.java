package com.qinyadan.article.transport;

import com.qinyadan.article.command.RpcCommand;
import com.qinyadan.article.command.RpcRequest;
import com.qinyadan.article.command.RpcResponse;
import com.qinyadan.article.transport.RpcWireFormatType.RpcHeartBeatCommand;
import com.taobao.gecko.core.buffer.IoBuffer;
import com.taobao.gecko.core.core.CodecFactory;
import com.taobao.gecko.core.core.Session;

public class RpcCodecFactory implements CodecFactory {
    static final byte REQ_MAGIC = (byte) 0x70;
    static final byte RESP_MAGIC = (byte) 0x71;

    static final class RpcDecoder implements Decoder {

        private static final String CURRENT_COMMAND = "CurrentCommand";


        public Object decode(IoBuffer buff, Session session) {
            if (!buff.hasRemaining()) {
                return null;
            }
            RpcCommand command = (RpcCommand) session.getAttribute(CURRENT_COMMAND);
            if (command != null) {
                if (command.decode(buff)) {
                    session.removeAttribute(CURRENT_COMMAND);
                    return command;
                } else {
                    return null;
                }
            } else {
                byte magic = buff.get();
                if (magic == REQ_MAGIC) {
                    command = new RpcRequest();
                } else {
                    command = new RpcResponse();

                }
                if (command.decode(buff)) {
                    return command;
                } else {
                    session.setAttribute(CURRENT_COMMAND, command);
                    return null;
                }
            }
        }

    }

    static final class RpcEncoder implements Encoder {

        public IoBuffer encode(Object message, Session session) {
            if (message instanceof RpcHeartBeatCommand) {
                return ((RpcHeartBeatCommand) message).request.encode();
            }
            return ((RpcCommand) message).encode();
        }

    }


    public Decoder getDecoder() {
        return new RpcDecoder();
    }


    public Encoder getEncoder() {
        return new RpcEncoder();
    }

}
