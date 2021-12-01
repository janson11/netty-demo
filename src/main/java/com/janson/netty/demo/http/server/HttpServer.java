package com.janson.netty.demo.http.server;

import com.janson.netty.demo.http.handler.ExceptionHandler;
import com.janson.netty.demo.http.handler.SampleInBoundHandler;
import com.janson.netty.demo.http.handler.SampleOutBoundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @Description: Http服务端启动类
 * @Author: Janson
 * @Date: 2021/8/15 15:02
 **/
public class HttpServer {

    public void start(int port) throws Exception {
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline()
                                    /*   // Http编解码
                                       .addLast("codec", new HttpServerCodec())
                                       // HttpContext压缩
                                       .addLast("compressor", new HttpContentCompressor())
                                       // HTTP消息聚合
                                       .addLast("aggregator", new HttpObjectAggregator(65536))
                                       // 自定义业务逻辑处理器
                                       .addLast("handler", new HttpServerHandler())*/
                                    .addLast(new SampleInBoundHandler("SampleInBoundHandlerA", false))
                                    .addLast(new SampleInBoundHandler("SampleInBoundHandlerB", false))
                                    .addLast(new SampleInBoundHandler("SampleInBoundHandlerC", true));
                            socketChannel.pipeline()
                                    .addLast(new SampleOutBoundHandler("SampleOutBoundHandlerA"))
                                    .addLast(new SampleOutBoundHandler("SampleOutBoundHandlerB"))
                                    .addLast(new SampleOutBoundHandler("SampleOutBoundHandlerC"))
                                    .addLast(new ExceptionHandler());
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            System.out.println("Http Server started, Listening on " + port);
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new HttpServer().start(8088);
    }
}
