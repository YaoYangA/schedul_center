package com.ylm.test.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author yaoyang
 * @Date 2019/3/1 14:00
 * @Description: 处理网络I/O事件，例如记录日志，对消息进行编解码
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class TimeServerHanler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //do something msg
        ByteBuf buf = (ByteBuf)msg;
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        String request = new String(data, "utf-8");
        System.out.println("Server: " + request);
        //写给客户端
        ctx.writeAndFlush(Unpooled.copiedBuffer("888".getBytes()));
        //.addListener(ChannelFutureListener.CLOSE);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
