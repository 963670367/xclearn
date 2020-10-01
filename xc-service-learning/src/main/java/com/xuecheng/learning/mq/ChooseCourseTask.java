package com.xuecheng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.service.ILearningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    private ILearningService learningService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE})
    public void receiveChoosecourseTask(XcTask xcTask, Message message, Channel channel) {
        // Message与Channel根据实际情况来写，可以不写
        LOGGER.info("receive choose course task,taskId:{}", xcTask.getId());
        // 接收到的消息id
        String id = xcTask.getId();
        try {
            // 添加选课,取到order发来的消息
            String requestBody = xcTask.getRequestBody();
            Map map = JSON.parseObject(requestBody, Map.class);
            String userId = (String) map.get("userId");
            String courseId = (String) map.get("courseId");
            String valid = (String) map.get("valid");
            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            if (map.get("startTime") != null) {
                startTime = sdf.parse((String) map.get("startTime"));
            }
            if (map.get("endTime") != null) {
                endTime = sdf.parse((String) map.get("endTime"));
            }
            // 添加选课
            ResponseResult addcourse = this.learningService.addcourse(userId, courseId, valid, startTime, endTime, xcTask);
            // 选课成功后发送响应消息
            if (addcourse.isSuccess()) {
                // 发送响应消息
                this.rabbitTemplate.convertAndSend(RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE, RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY, xcTask);
                LOGGER.info("send finish choose taskId:{}", id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("send finish choose taskId:{}", id);
        }
    }
}
