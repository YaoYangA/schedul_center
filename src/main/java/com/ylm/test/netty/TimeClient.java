package com.ylm.test.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author yaoyang
 * @Date 2019/3/1 14:21
 * @Description:
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class TimeClient {

    public  void  connect(int port,String host) throws Exception{
        // 配置客户端NIO线程组
        NioEventLoopGroup group = new NioEventLoopGroup();


        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class) // 主要是客户端接收SocketChannel用的
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            // 发起异步连接操作
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().writeAndFlush(Unpooled.copiedBuffer("777".getBytes()));
            //等待客户端链路关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅推出，释放NIO线程组
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        new TimeClient().connect(8081,"127.0.0.1");
    }
}
