package com.janson.netty.demo.http.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Description: 字符串编码器
 * @Author: Janson
 * @Date: 2021/8/20 9:24
 **/
public class StringToByteEncoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.getBytes());
    }
}
