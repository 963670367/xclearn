package com.xuecheng.search.controller;

import com.xuecheng.api.course.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.response.QueryResponseResult;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.search.service.IEsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/search/course")
public class EsCourseController implements EsCourseControllerApi {

    @Autowired
    private IEsCourseService esCourseService;

    /**
     *
     * @param page
     * @param size
     * @param courseSearchParam 不加@RequestBody 请求为
     * @return
     * @throws IOException
     */
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult<CoursePub> list(@PathVariable("page") int page,@PathVariable("size")  int size, CourseSearchParam courseSearchParam) throws IOException {
        return this.esCourseService.list(page,size,courseSearchParam);
    }

    @Override
    @GetMapping("/getall/{id}")
    public Map<String, CoursePub> getall(@PathVariable("id") String id) {
        return this.esCourseService.getall(id);
    }

    @Override
    @GetMapping("/getmedia/{teachplanId}")
    public TeachplanMediaPub getmedia(@PathVariable("teachplanId") String teachplanId){
        // 将课程计划id放到数组中，为调用service做准备
        String[] teachplanIds = new String[]{teachplanId};
        // 通过service查询ES获取课程媒资信息
        QueryResponseResult<TeachplanMediaPub> mediaPubQueryResponseResult = this.esCourseService.getmedia(teachplanIds);
        QueryResult<TeachplanMediaPub> queryResult = mediaPubQueryResponseResult.getQueryResult();
        if (queryResult!=null && queryResult.getList()!=null && queryResult.getList().size()>0) {
            // 返回课程计划对应课程媒资,只返回一个
            return queryResult.getList().get(0);
        }
        return new TeachplanMediaPub();
    }
}
