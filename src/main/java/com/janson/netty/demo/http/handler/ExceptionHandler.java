package com.janson.netty.demo.http.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description: 用户自定义处理器
 * @Author: Janson
 * @Date: 2021/8/18 23:28
 **/
public class ExceptionHandler extends ChannelDuplexHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof RuntimeException) {
            System.out.println("Handle Business Exception Success.  message:" + cause.getMessage());
        }
    }
}
