package com.ylm.common.constant;

/**
 * @author yaoyang
 * @Date 2019/3/4 10:53
 * @Description:
 * @versio 1.0
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

    // 请求成功
    int SUCCESS = 200;
    // 请求参数错误
    int PARAM_ERROR = 401;

    // 添加失败
    int FAIL = 500;

    //添加定时任务的路径
    String TASK_ADD_PATH = "/task/add";

    //EMPTY 空字符串
    String EMPTY = "";

    // 固定组
    String GROUP = "CMPAY_GROUP";

}
