package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.service.ICourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDao {
    @Autowired
    CourseBaseRepository courseBaseRepository;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    ICourseService courseService;

    @Test
    public void testCourseBaseRepository() {
        Optional<CourseBase> optional = courseBaseRepository.findById("402885816240d276016240f7e5000002");
        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            System.out.println(courseBase);
        }

    }

    @Test
    public void testCourseMapper() {
        CourseBase courseBase = courseMapper.findCourseBaseById("402885816240d276016240f7e5000002");
        System.out.println(courseBase);
    }

    @Test
    public void testPageHelper() {
        PageHelper.startPage(1, 10);  // 查询第一页，每页显示10条记录
        CourseListRequest courseListRequest = new CourseListRequest();
//        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(new CourseListRequest());
        List<CourseInfo> result = courseListPage.getResult();
        long total = courseListPage.getTotal();
        System.out.println(courseListPage);
        System.out.println("total: " + total);
    }

    @Test
    public void testTeachplanMapper() {
        TeachplanNode teachplanNode = teachplanMapper.selectList("4028e581617f945f01617f9dabc40000");
        System.out.println(teachplanNode);
    }

    @Test
    public void testaddTeachplan() {
        Teachplan teachplan = new Teachplan();
        teachplan.setPname("测试Pname");
        teachplan.setCourseid("297e7c7c62b888f00162b8a965510001");
        ResponseResult responseResult = this.courseService.addTeachplan(teachplan);
        System.out.println(responseResult);
    }
}
