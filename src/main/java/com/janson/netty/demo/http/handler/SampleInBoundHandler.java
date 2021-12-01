package com.janson.netty.demo.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Description: 入站 样例
 * @Author: Janson
 * @Date: 2021/8/18 23:03
 **/
public class SampleInBoundHandler extends ChannelInboundHandlerAdapter {
    private final String name;
    private final boolean flush;

    public SampleInBoundHandler(String name, boolean flush) {
        this.name = name;
        this.flush = flush;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InBoundHandler:" + name);
        if (flush) {
            ctx.channel().writeAndFlush(msg);
        } else {
            throw new RuntimeException("InBoundHandler:" + name);
//            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("InBoundHandlerException:"+name);
        ctx.fireExceptionCaught(cause);
    }
}
