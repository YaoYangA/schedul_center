package com.ylm.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yaoyang
 * @Date 2019/3/8 9:56
 * @Description: http服务端
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class HttpServer implements Runnable {

    private Logger log = LoggerFactory.getLogger(this.getClass());


    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            NioEventLoopGroup group = new NioEventLoopGroup();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            System.out.println("initChannel ch:" + ch);
                            ch.pipeline()
                                    .addLast("decoder", new HttpRequestDecoder())   // 1
                                    .addLast("encoder", new HttpResponseEncoder())  // 2
                                    .addLast("aggregator", new HttpObjectAggregator(512 * 1024))    // 3
                                    .addLast("handler", new HttpHandler());        // 4
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

            b.bind(port).sync();
        } catch (Exception e) {
            log.info("启动添加定时任务接口出现异常，异常信息：{}",e.getMessage());
        }
    }
}
