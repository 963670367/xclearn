package com.xuecheng.order.service.impl;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import com.xuecheng.order.service.ITaskService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements ITaskService {
    @Autowired
    private XcTaskRepository xcTaskRepository;
    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 取出前n条任务，取出指定时间之前处理的任务
     *
     * @param updateTime
     * @param n
     * @return
     */
    @Override
    public List<XcTask> findTaskList(Date updateTime, int n) {
        // 设置分页参数，取出前n条记录
        Pageable pageable = new PageRequest(0, n);
        Page<XcTask> xcTasks = this.xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);

        return xcTasks.getContent();
    }

    /**
     * 发送消息
     *
     * @param xcTask     任务对象
     * @param ex         交换机id
     * @param routingKey
     */
    @Transactional
    @Override
    public void publish(XcTask xcTask, String ex, String routingKey) {
        Optional<XcTask> optional = this.xcTaskRepository.findById(xcTask.getId());
        if (optional.isPresent()) {
            XcTask xcTask1 = optional.get();
            this.rabbitTemplate.convertAndSend(ex,routingKey,xcTask1);
            // 更新任务时间为当前时间
            xcTask1.setUpdateTime(new Date());
            this.xcTaskRepository.save(xcTask1);
        }
    }


    @Transactional
    @Override
    public int getTask(String taskId,int version){
        // 加update加1
        int i = this.xcTaskRepository.updateTaskVersion(taskId,version);
        return i;
    }

    /**
     * 删除任务
     * @param taskId
     */
    @Transactional
    @Override
    public void finishTask(String taskId){
        Optional<XcTask> optional = this.xcTaskRepository.findById(taskId);
        if (optional.isPresent()) {
            // 当前任务
            XcTask xcTask = optional.get();
            xcTask.setDeleteTime(new Date());
            // 历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            this.xcTaskHisRepository.save(xcTaskHis);
            this.xcTaskRepository.delete(xcTask);
        }
    }
}
