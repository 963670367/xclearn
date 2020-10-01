package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.response.QueryResponseResult;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.Map;

@Api(value = "课程搜索", description = "课程搜索", tags = {"课程搜索"})
public interface EsCourseControllerApi {
    @ApiOperation("课程搜索")
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) throws IOException;

    @ApiOperation("根据id查询课程信息")
    public Map<String,CoursePub> getall(String id);

    @ApiOperation("根据课程计划查询媒资信息")
    TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId);
}
