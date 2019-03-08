package com.ylm.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yaoyang
 * @Date 2019/3/7 17:18
 * @Description:
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class RequestParser {
    private FullHttpRequest fullReq;

    /**
     * 构造一个解析器
     * @param req
     */
    public RequestParser(FullHttpRequest req) {
        this.fullReq = req;
    }

    /**
     * 解析请求参数
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     *
     * @throws IOException
     */
    public Map<String, Object> parse() throws IOException {
        HttpMethod method = fullReq.method();
        HttpHeaders headers = fullReq.headers();
        String contentType = headers.get(HttpHeaderNames.CONTENT_TYPE);
        Map<String, Object> parmMap = new HashMap<>();

        if (HttpMethod.GET.equals(method)) {
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(fullReq.uri());
            decoder.parameters().entrySet().forEach( entry -> {
                // entry.getValue()是一个List, 只取第一个元素
                parmMap.put(entry.getKey(), entry.getValue().get(0));
            });
        } else if (HttpMethod.POST.equals(method)) {
            // 判断是否是表单提交
            if (!contentType.equals("application/json")){
                // 是POST请求
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullReq);
                decoder.offer(fullReq);

                List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

                for (InterfaceHttpData parm : parmList) {

                    Attribute data = (Attribute) parm;
                    parmMap.put(data.getName(), data.getValue());
                }
            }else{
                ByteBuf content = fullReq.content();
                JSONObject param = JSON.parseObject(content.toString(Charset.forName("UTF-8")));
                return  param;
            }
        }
        return  parmMap;
    }
}
