package com.ylm.job;

import com.ylm.http.client.HttpClient;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yaoyang
 * @Date 2019/3/4 10:28
 * @Description: 访问cmpay对账接口的Job
 * @versio 1.0
 * 深圳易联联盟技术有限公司
 * Copyright (c) 2018 All Rights Reserved.
 */
public class CmpayJob implements Job {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            String url = jobDataMap.getString("url");
            log.info("执行定时任务，url：{}",url);
            HttpClient.sendRequest(url);
        } catch (Exception e) {
            log.error("指定定时任务出现异常，异常信息：{}",e.getMessage());
        }
    }
}
