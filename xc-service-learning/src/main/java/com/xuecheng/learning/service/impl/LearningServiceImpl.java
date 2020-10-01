package com.xuecheng.learning.service.impl;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.learning.GetMediaResult;
import com.xuecheng.framework.domain.learning.XcLearningCourse;
import com.xuecheng.framework.domain.learning.response.LearningCode;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.client.CourseSearchClient;
import com.xuecheng.learning.dao.XcLearningCourseRepository;
import com.xuecheng.learning.dao.XcTaskHisRepository;
import com.xuecheng.learning.service.ILearningService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class LearningServiceImpl implements ILearningService {

    @Autowired
    private CourseSearchClient courseSearchClient;

    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    private XcLearningCourseRepository xcLearningCourseRepository;

    /**
     * 查询视频播放地址
     *
     * @param courseId
     * @param teachplanId
     * @return
     */
    @Override
    public GetMediaResult getMedia(String courseId, String teachplanId) {
        // 校验学生学习权限

        // 远程调用搜索服务查询课程计划对应的课程媒资信息
        TeachplanMediaPub teachplanMediaPub = this.courseSearchClient.getMedia(teachplanId);
        if (teachplanMediaPub == null || StringUtils.isEmpty(teachplanMediaPub.getMediaUrl())) {
            // 获取学习地址错误
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        return new GetMediaResult(CommonCode.SUCCESS, teachplanMediaPub.getMediaUrl());
    }

    /**
     * 添加选课
     *
     * @param userId
     * @param courseId
     * @param valid
     * @param startTime
     * @param endTime
     * @param xcTask
     * @return
     */
    @Transactional
    @Override
    public ResponseResult addcourse(String userId, String courseId, String valid, Date startTime, Date endTime, XcTask xcTask) {
        if (StringUtils.isEmpty(courseId)) {
            ExceptionCast.cast(LearningCode.LEARNING_GETMEDIA_ERROR);
        }
        if (StringUtils.isEmpty(userId)) {
            ExceptionCast.cast(LearningCode.CHOOSECOURSE_USERISNULL);
        }
        if (xcTask == null || StringUtils.isEmpty(xcTask.getId())) {
            ExceptionCast.cast(LearningCode.CHOOSECOUESE_TASKISNULL);
        }
        // 查询历史任务
        Optional<XcTaskHis> optional = this.xcTaskHisRepository.findById(xcTask.getId());
        if (optional.isPresent()) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        XcLearningCourse xcLearningCourse = this.xcLearningCourseRepository.findXcLearningCourseByUserIdAndCourseId(userId, courseId);
        if (xcLearningCourse==null) { // 没有选课记录则添加
             xcLearningCourse = new XcLearningCourse();
             xcLearningCourse.setCourseId(courseId);
             xcLearningCourse.setEndTime(endTime);
             xcLearningCourse.setStartTime(startTime);
             xcLearningCourse.setUserId(userId);
             xcLearningCourse.setStatus("501001");
             xcLearningCourse.setValid(valid);
             this.xcLearningCourseRepository.save(xcLearningCourse);
        } else{ // 有选课记录则更新日期,userId,courseId不需要，我们是根据他们查的
            xcLearningCourse.setValid(valid);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setStatus("501001");
            this.xcLearningCourseRepository.save(xcLearningCourse);
        }
        // 向历史任务中插入记录，历史任务来自于xc_task
        Optional<XcTaskHis> optional1 = this.xcTaskHisRepository.findById(xcTask.getId());
        if (!optional1.isPresent()) {
            // 添加历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }


}
