package com.ylm.server.typeServer;

import com.ylm.server.config.Beans;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author yaoyang
 * @Date 2019/3/7 11:59
 * @Description:
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class ServerThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private Beans beans = new Beans();

    private ServerBootstrap serverBootstrap = beans.bootstrap();

    private Channel serverChannel ;

    private Integer port;

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            log.info("服务端启动，端口号:{}",port);
            InetSocketAddress tcpIpAndPort = beans.tcpPort(port);
            serverChannel =  serverBootstrap.bind(tcpIpAndPort).sync().channel().closeFuture().sync().channel();
        } catch (InterruptedException e) {
            log.error("服务端启动出现异常，异常信息：{}",e.getMessage());
        }
    }
}
