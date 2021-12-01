package com.janson.netty.demo.http.encoder;

import com.janson.netty.demo.http.response.ResponseSample;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Description: 响应结果编码器
 * @Author: Janson
 * @Date: 2021/8/21 18:50
 **/
public class ResponseSampleEncoder extends MessageToByteEncoder<ResponseSample> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseSample msg, ByteBuf out) throws Exception {
        if (msg != null) {
            out.writeBytes(msg.getCode().getBytes());
            out.writeBytes(msg.getData().getBytes());
            out.writeLong(msg.getTimeSample());
        }
    }
}
