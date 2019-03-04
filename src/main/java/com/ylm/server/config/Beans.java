package com.ylm.server.config;

import com.ylm.server.channelInitializer.ServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @package com.netty.server.config
 * @Date Created in 2019/2/23 13:01
 * @Author myzf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beans {

    /*根据名称装配，防止和客户端的ChannelInitializer冲突报错*/
    private ServerChannelInitializer serverChannelInitializer = new ServerChannelInitializer();

    private String host = "127.0.0.1";

    private int port = 19999;

    private int bossCount = 2;

    private int workerCount = 20;

    private boolean keepAlive = true;

    private int backlog = 100;

    /**
     * netty服务器启动帮助类
    */
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(serverChannelInitializer);
        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
        for (ChannelOption option : keySet) {
            b.option(option, tcpChannelOptions.get(option));
        }
        return b;
    }



    /**
     * netty服务器相关设置
     */
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();

        /*是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）
        并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活
        */
        options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
         /*
         BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，
        用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，
        Java将使用默认值50
        */
        options.put(ChannelOption.SO_BACKLOG, backlog);

        /*
        在TCP/IP协议中，无论发送多少数据，总是要在数据前面加上协议头，
        同时，对方接收到数据，也需要发送ACK表示确认。
        为了尽可能的利用网络带宽，TCP总是希望尽可能的发送足够大的数据。
        这里就涉及到一个名为Nagle的算法，该算法的目的就是为了尽可能发送大块数据，避免网络中充斥着许多小数据块。
        */
        options.put(ChannelOption.TCP_NODELAY,true);

        return options;
    }

    /*用来监控tcp链接 指定线程数 默认是1 用默认即可*/
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossCount);
    }

    /*处理io事件 一定要多线程效率高 源码中默认是cpu核数*2 */
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerCount);
    }

    /*定义使用tcp连接方式*/
    public InetSocketAddress tcpPort(Integer tcpPort) {
        if (tcpPort != null){
            port = tcpPort;
        }
        return new InetSocketAddress(host,port);
    }

}
