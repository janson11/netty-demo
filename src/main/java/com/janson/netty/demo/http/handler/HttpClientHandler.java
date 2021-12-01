package com.janson.netty.demo.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.CharsetUtil;

/**
 * @Description: Http 客户端业务处理器
 * @Author: Janson
 * @Date: 2021/8/15 23:30
 **/
public class HttpClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf byteBuf = content.content();
            System.out.println(byteBuf.toString(CharsetUtil.UTF_8));
            byteBuf.release();
        }
    }
}
