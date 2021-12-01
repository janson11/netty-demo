package com.janson.netty.demo.http.handler;

import com.janson.netty.demo.http.response.ResponseSample;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @Description: 响应结果编码器
 * @Author: Janson
 * @Date: 2021/8/21 18:37
 **/
public class RequestSampleHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String data = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);
        ResponseSample response = new ResponseSample("OK", data, System.currentTimeMillis());
        ctx.channel().writeAndFlush(response);
    }
}
