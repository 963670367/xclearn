package com.xuecheng.order.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.ITaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class ChooseCourseTask {
    private static Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

//    // 定义任务调动策略
//    @Scheduled(fixedRate = 3000)
//    public void task1(){
//        LOGGER.info("=============测试定时任务1开始==============");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        LOGGER.info("=============测试定时任务1结束==============");
//    }
//
//    // 定义任务调动策略
//    @Scheduled(fixedRate = 2000)
//    public void task2(){
//        LOGGER.info("=============测试定时任务2开始==============");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        LOGGER.info("=============测试定时任务2结束==============");
//    }
    @Autowired
    private ITaskService taskService;

    // 每隔1分钟扫描消息表，向mq发送消息
    @Scheduled(fixedDelay = 6000)
    public void sendChooseCourseTask(){
        // 取出1分钟之前的消息
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE,-1); // 在当前时间的基础上减1
        Date time = calendar.getTime(); // 得到1分钟之前的时间
        List<XcTask> taskList = taskService.findTaskList(time, 1000);

        // 遍历任务列表
        for (XcTask xcTask : taskList) {
            // 任务id
            String taskId = xcTask.getId();
            // 版本号
            Integer version = xcTask.getVersion();
            // 调用乐观锁方法校验任务是否可以执行
            if (this.taskService.getTask(taskId,version)>0) {
                // 发送选课消息，将version+1
                this.taskService.publish(xcTask,xcTask.getMqExchange(),xcTask.getMqRoutingkey());
                LOGGER.info("send choose course task id:{}",xcTask.getId());
            }
        }
    }

    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE})
    public void receiveFinishChoosecourseTask(XcTask xcTask, Message message, Channel channel) {
        LOGGER.info("receive choose course task,taskId:{}", xcTask.getId());
        // 接收到的消息id
        if (xcTask!=null && StringUtils.isNotEmpty(xcTask.getId())) {
            String id = xcTask.getId();
            // 删除任务，添加历史任务
            this.taskService.finishTask(id);
        }
    }
}
