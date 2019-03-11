package com.ylm;

import com.ylm.client.heart.NettyClient;
import com.ylm.common.constant.Constant;
import com.ylm.server.typeServer.TCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yaoyang
 * @Date 2019/3/4 10:14
 * @Description: 主启动类
 * @versio 1.0
 * Copyright (c) 2018 All Rights Reserved.
 */
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        if (args.length == 2) {
//            String flag = Constant.SERVER_FLAG;
//            String port = "19999";
            String flag = args[0];
            String port = args[1];
            try {
                if (null != flag && !"".equals(flag)) {

                    switch (flag) {
                        // 如果是服务端，那就启动服务端，并且开始执行定时任务
                        case Constant.SERVER_FLAG:
                            TCPServer tcpServer = new TCPServer();
                            tcpServer.start(port);
                            break;
                        // 如果是客户端，那就启动客户端,但不启动定时任务
                        case Constant.CLIENT_FLAG:
                            NettyClient client = new NettyClient();
                            client.run(port);
                            break;
                        default:
                            System.out.println("启动失败，flag参数错误");
                            break;
                    }
                }
            } catch (Exception e) {
                LOG.error("启动出现异常，标志：{}，端口：{}", flag, port);
            }
        }else {
            System.out.println("启动时请附加正确的参数");
        }
    }
}
