package com.ylm.http;

import com.alibaba.fastjson.JSON;
import com.ylm.common.constant.Constant;
import com.ylm.test.HelloJob;
import com.ylm.util.CheckUtils;
import com.ylm.util.QuartzUtils;
import com.ylm.util.RequestParser;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yaoyang
 * @Date 2019/3/8 10:06
 * @Description:
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final static Logger LOG = LoggerFactory.getLogger(HttpHandler.class);


    private AsciiString contentType = HttpHeaderValues.APPLICATION_JSON;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String uri = msg.uri();
        HttpMethod method = msg.method();
        // 设置response
        DefaultFullHttpResponse response ;

        if (!uri.equals(Constant.TASK_ADD_PATH) || !method.equals(HttpMethod.POST)){
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.NOT_FOUND);
        }else {
            HashMap<String, Object> result = new HashMap<>();
            result.put("code",Constant.SUCCESS);
            result.put("msg","添加定时任务成功");

            // post请求参数
            RequestParser requestParser = new RequestParser(msg);
            Map<String, Object> params = requestParser.parse();
            // 任务名称
            Object jobName = params.get("jobName");
            // 任务描述
            Object description = params.get("description");
            // cron表达式
            Object cron = params.get("cron");
            // 要被访问的url
            Object url = params.get("url");

            if (params.size()!=4 || CheckUtils.isNullOrEmpty(jobName) || CheckUtils.isNullOrEmpty(cron) || CheckUtils.isNullOrEmpty(url)){
                result.put("code",Constant.PARAM_ERROR);
                result.put("msg","参数错误");
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
                LOG.info("添加定时任务参数校验失败：{}",params);
            }else {

                try {
                    QuartzUtils quartzUtils = new QuartzUtils();
                    quartzUtils.addJob(jobName.toString(),Constant.GROUP,description.toString(), HelloJob.class,cron.toString(),url.toString());
                    response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.OK,
                            Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
                    LOG.info("添加定时任务成功,任务名称：{}，任务描述：{}",jobName,description);
                } catch (Exception e) {
                    LOG.error("添加定时任务出现异常，异常信息：{}",e.getMessage());
                    result.put("code",Constant.FAIL);
                    result.put("msg","添加失败，请联系运维人员");
                    response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                            HttpResponseStatus.OK,Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
                }
            }
        }
        HttpHeaders heads = response.headers();
        heads.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
        heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()); // 3
        heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(null != cause) cause.printStackTrace();
        if(null != ctx) ctx.close();
    }
}
