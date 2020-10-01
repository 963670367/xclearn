package com.xuecheng.manage_course.web.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {
    @Autowired
    ICourseService courseService;

    // @RequestBody将json解析为Java 对象
    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return this.courseService.addTeachplan(teachplan);
    }

    // 查询课程计划
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return this.courseService.findTeachplanList(courseId);
    }

    @Override
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult<CourseInfo> findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        QueryResponseResult<CourseInfo> courseList = this.courseService.findCourseList(page, size, courseListRequest);
        return courseList;
    }

    @Override
    public CategoryNode findList() {
        return null;
    }

    @Override
    @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseBaseById(@PathVariable("courseId") String courseId) throws RuntimeException {
        return this.courseService.getCourseBaseById(courseId);
    }

    @Override
    @PutMapping("/coursebase/update/{id}")
    public ResponseResult updateCourseBase(@PathVariable("id") String id, @RequestBody CourseBase courseBase) {
        return this.courseService.updateCourseCourseBase(id, courseBase);
    }

    @Override
    @GetMapping("/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return this.courseService.getCourseMarketById(courseId);
    }

    @Override
    @PutMapping("/coursemarket/update/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {
        return this.courseService.updateCourseMarket(id, courseMarket);
    }

    @Override
    @PostMapping("/coursebase/add")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return this.courseService.addCourseBase(courseBase);
    }

    // 添加课程图片
    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId,@RequestParam("pic") String pic) {
        // 保存课程图片
        return this.courseService.saveCoursePic(courseId,pic);
    }

    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic getCoursePic(@PathVariable("courseId") String courseId) {
        return this.courseService.getCoursePic(courseId);
    }
    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId){
        return this.courseService.deleteCoursePic(courseId);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseview(@PathVariable("id") String id) {
        return this.courseService.getCourseView(id);
    }

    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        CoursePublishResult coursePublishResult = this.courseService.preview(id);
        return coursePublishResult;
    }

    /**
     * 发布课程
     * @param id
     * @return
     */
    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable("id") String id) {
        return this.courseService.publish(id);
    }

    /**
     * 保存媒资信息
     * @param teachplanMedia
     * @return
     */
    @Override
    @PostMapping("/savemedia")
    public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
        return this.courseService.savemedia(teachplanMedia);
    }

}
