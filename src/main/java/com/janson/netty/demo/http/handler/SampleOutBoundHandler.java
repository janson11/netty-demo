package com.janson.netty.demo.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @Description: 出站 样例
 * @Author: Janson
 * @Date: 2021/8/18 23:08
 **/
public class SampleOutBoundHandler extends ChannelOutboundHandlerAdapter {

    private final String name;

    public SampleOutBoundHandler(String name) {
        this.name = name;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutBoundHandler:" + name);
        super.write(ctx, msg, promise);
    }
}
