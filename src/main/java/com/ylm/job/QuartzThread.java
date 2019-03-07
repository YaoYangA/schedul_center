package com.ylm.job;

import com.ylm.util.QuartzUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yaoyang
 * @Date 2019/3/6 17:16
 * @Description: 定时任务启动线程
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class QuartzThread implements Runnable {
    public Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    public void run() {
        try {
            QuartzUtils quartzUtils = new QuartzUtils();
            Scheduler scheduler = quartzUtils.getScheduler();
            scheduler.start();
            scheduler.resumeAll();
        } catch (SchedulerException e) {
            log.error("定时任务启动异常，异常信息：{}",e.getMessage());
        }
    }
}
