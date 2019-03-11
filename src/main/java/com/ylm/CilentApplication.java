package com.ylm;

import com.ylm.client.heart.NettyClient;

/**
 * @author yaoyang
 * @Date 2019/3/5 14:00
 * @Description:
 * @versio 1.0
 * Copyright (c) 2018 All Rights Reserved.
 */
public class CilentApplication {

    public static void main(String[] args) {
        try {
            NettyClient client = new NettyClient();
            client.run("8081");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
