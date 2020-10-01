package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CoursePic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePicRepository extends JpaRepository<CoursePic,String> {
    // 提供的删除方法没有返回值无法确定是否删除成功，这里我们自定义方法
    // 删除成功返回受到影响的条数，否则返回0
    long deleteByCourseid(String courseid);
}
