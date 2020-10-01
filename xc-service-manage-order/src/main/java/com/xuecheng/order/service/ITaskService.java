package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;

import java.util.Date;
import java.util.List;

public interface ITaskService {
    /**
     * 取出前n条任务，取出指定时间之前处理的任务
     * @param updateTime
     * @param n
     * @return
     */
    public List<XcTask> findTaskList(Date updateTime,int n);

    /**
     * 发送消息
     * @param xcTask    任务对象
     * @param ex        交换机id
     * @param routingKey
     */
    public void publish(XcTask xcTask,String ex,String routingKey);


    public int getTask(String taskId,int version);

    /**
     * 删除任务
     * @param taskId
     */
    public void finishTask(String taskId);
}
