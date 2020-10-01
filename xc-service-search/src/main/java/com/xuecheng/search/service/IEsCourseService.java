package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.response.QueryResponseResult;
import com.xuecheng.framework.domain.search.CourseSearchParam;

import java.util.Map;

public interface IEsCourseService {

    /**
     * 课程搜索
     * @param page
     * @param size
     * @param courseSearchParam
     * @return
     */
    QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam);

    /**
     * 根据id查询课程信息
     * @param id
     * @return
     */
    Map<String, CoursePub> getall(String id);

    /**
     * 获取课程媒资信息
     * @param teachplanIds
     * @return
     */
    QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds);
}
