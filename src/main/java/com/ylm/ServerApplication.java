package com.ylm;

import com.ylm.server.typeServer.TCPServer;

/**
 * @author yaoyang
 * @Date 2019/3/5 14:01
 * @Description:
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class ServerApplication {
    public static void main(String[] args) {
        try {
            TCPServer tcpServer = new TCPServer();
            tcpServer.start("8080");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
