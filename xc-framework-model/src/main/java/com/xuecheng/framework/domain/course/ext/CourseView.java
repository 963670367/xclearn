package com.xuecheng.framework.domain.course.ext;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.domain.course.CoursePic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor  // 被远程调用，需要无参数构造方法
public class CourseView implements Serializable {
    CourseBase courseBase; // 基本信息
    CourseMarket courseMarket; // 课程营销
    CoursePic coursePic; // 课程图片
    TeachplanNode teachplanNode; // 教学计划
}
