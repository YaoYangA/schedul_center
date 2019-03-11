package com.ylm.http.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yaoyang
 * @Date 2019/3/11 15:21
 * @Description:
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class HttpSnoopClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger log = LoggerFactory.getLogger(HttpSnoopClientHandler.class);


    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            log.info("如果响应类型为HttpResponse，响应状态：{}，HTTP协议版本：{}",response.status(),response.protocolVersion());
        }
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            JSONObject result = JSON.parseObject(content.content().toString(CharsetUtil.UTF_8));
            log.info("cmpay对账接口返回结果：{}",result);
            if (content instanceof LastHttpContent) {
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
