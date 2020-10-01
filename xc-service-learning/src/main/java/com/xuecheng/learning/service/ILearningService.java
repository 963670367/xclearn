package com.xuecheng.learning.service;

import com.xuecheng.framework.domain.learning.GetMediaResult;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;

import java.util.Date;

public interface ILearningService {
    /**
     * 查询视频播放地址
     * @param courseId
     * @param teachplanId
     * @return
     */
    GetMediaResult getMedia(String courseId, String teachplanId);

    /**
     * 完成选课
     * @param userId
     * @param courseId
     * @param valid
     * @param startTime
     * @param endTime
     * @param xcTask
     * @return
     */
    public ResponseResult addcourse(String userId, String courseId, String valid, Date startTime, Date endTime, XcTask xcTask);



}
