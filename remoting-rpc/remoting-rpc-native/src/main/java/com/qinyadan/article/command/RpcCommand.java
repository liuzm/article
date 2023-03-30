package com.qinyadan.article.command;

import com.taobao.gecko.core.buffer.IoBuffer;


public interface RpcCommand {
    public boolean decode(IoBuffer buffer);

    public IoBuffer encode();
}
