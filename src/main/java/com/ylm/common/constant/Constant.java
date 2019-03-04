package com.ylm.common.constant;

/**
 * @author yaoyang
 * @Date 2019/3/4 10:53
 * @Description:
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public interface Constant {

    String SERVER_FLAG = "server";

    String CLIENT_FLAG = "client";

    // 服务器读操作空闲10秒
    int READER_IDLE_TIME_SECONDS = 10;

    // 服务器写操作空闲0秒
    int WRITER_IDLE_TIME_SECONDS = 0;

    // 服务器全部操作空闲0秒
    int ALL_IDLE_TIME_SECONDS = 0;
}
