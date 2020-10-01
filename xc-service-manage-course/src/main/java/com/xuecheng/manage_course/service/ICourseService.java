package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

public interface ICourseService {
    // 查询课程计划
    public TeachplanNode findTeachplanList(String courseId);

    // 添加课程计划
    ResponseResult addTeachplan(Teachplan teachplan);

    // 课程列表分页查询
    public QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest);

    // 添加课程提交
    public AddCourseResult addCourseBase(CourseBase courseBase);

    // 根据id查询课程基本信息
    public CourseBase getCourseBaseById(String courseId);

    // 根据id更新课程基本信息
    ResponseResult updateCourseCourseBase(String id, CourseBase courseBase);

    // 根据课程id查询课程营销信息
    CourseMarket getCourseMarketById(String courseId);

    // 根据课程id更新课程营销信息
    ResponseResult updateCourseMarket(String id, CourseMarket courseMarket);

    // 保存课程图片
    ResponseResult saveCoursePic(String courseId, String pic);

    // 查看课程图片
    CoursePic getCoursePic(String courseId);

    // 删除课程图片
    ResponseResult deleteCoursePic(String courseId);

    // 课程视图查询
    CourseView getCourseView(String id);

    // 预览页面
    CoursePublishResult preview(String id);

    // 通过id查找课程基本信息
    CourseBase findCourseBaseById(String courseId);

    // 课程发布
    CoursePublishResult publish(String id);

    // 保存媒资信息
    ResponseResult savemedia(TeachplanMedia teachplanMedia);
}
