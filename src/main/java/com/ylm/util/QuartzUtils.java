package com.ylm.util;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author yaoyang
 * @Date 2019/2/12 16:04
 * @Description:
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class QuartzUtils {

    private final static Logger logger = LoggerFactory.getLogger(QuartzUtils.class);

    private Scheduler scheduler = null;

    public Scheduler getScheduler() {
        return scheduler;
    }

    public QuartzUtils() {
        try {
            this.scheduler = new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            logger.error("获取调度器出错，异常信息{}", e);
        }
    }


    public void addJob(String name, String group, String description, Class<? extends Job> clazz, String cronExpression, String url) throws Exception {
        JobKey jobKey = new JobKey(name, group);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
        // 构造任务
        JobDetail job = newJob(clazz)
                .withIdentity(name, group)
                .withDescription(description)
                .usingJobData("url", url)
                .build();

        // 构造任务触发器
        Trigger trg = newTrigger()
                .withIdentity(name, group)
                .withSchedule(cronSchedule(cronExpression))
                .build();
        // 将作业添加到调度器
        scheduler.scheduleJob(job, trg);
    }

    /**
     * 暂停调度中所有的job任务
     *
     * @throws SchedulerException
     */
    public void pauseAll() throws SchedulerException {
        scheduler.pauseAll();
    }

    /**
     * 恢复调度中所有的job的任务
     *
     * @throws SchedulerException
     */
    public void resumeAll() throws SchedulerException {
        scheduler.resumeAll();
    }

}
