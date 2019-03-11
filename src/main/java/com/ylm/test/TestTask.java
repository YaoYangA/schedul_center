package com.ylm.test;

import com.ylm.util.QuartzUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * @author yaoyang
 * @Date 2019/2/27 9:17
 * @Description:
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class TestTask {


    public static void main(String[] args) throws SchedulerException, InterruptedException {
        QuartzUtils quartzUtils = new QuartzUtils();
//        quartzUtils.resumeAll();
//        quartzUtils.addJob("job1","group1","测试",HelloJob.class,"* * * * * ? *","www.baidu.com");
//        quartzUtils.addJob("job2","group1","银行实收对账",HelloJob.class,"0/2 * * * * ? *","www.sina.com");
//
//        quartzUtils.addJob("job2","group2",Job2.class,"* * * * * ? *");
        Scheduler scheduler = quartzUtils.getScheduler();
//        scheduler.start();
        scheduler.clear();
        scheduler.shutdown();
//        scheduler.deleteJob(new JobKey("job2","group2"));
//        scheduler.resumeAll();
//
//        Thread.sleep(10000);
//        scheduler.pauseAll();
//        scheduler.pauseJob(new JobKey("job1","group1"));
    }
}
