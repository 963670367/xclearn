package com.xuecheng.manage_course.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.course.response.QueryResponseResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import com.xuecheng.manage_course.service.ICourseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements ICourseService {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMarketRepository courseMarketRepository;

    @Autowired
    CoursePicRepository coursePicRepository;

    @Autowired
    CoursePubRepository coursePubRepository;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    CmsPageClient cmsPageClient;

    @Value("${course-publish.siteId}")
    private String publish_siteId;

    @Value("${course-publish.templateId}")
    private String publish_templateId;

    @Value("${course-publish.previewUrl}")
    private String publish_previewUrl;

    @Value("${course-publish.pageWebPath}")
    private String publish_page_WebPath;

    @Value("${course-publish.pagePhyscialPath}")
    private String publish_page_PhyscialPath;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;

    @Autowired
    private TeachplanMediaRepository teachplanMediaRepositroy;
    
    @Autowired
    private TeachplanMediaPubRepository teachplanMediaPubRepositroy;

    // 查询课程计划
    public  TeachplanNode findTeachplanList(String courseId) {
        return this.teachplanMapper.selectList(courseId);
    }

    // 添加课程计划
    // 先看看当前提交的节点是否有课程id和课程计划名称，
    // 如果有则可以插入节点了，当前节点的信息根据父节点信息进行设置
    @Override
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        // 校验课程id和课程计划名称
        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getCourseid()) ||
                StringUtils.isEmpty(teachplan.getPname())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        // 取出课程id
        String courseid = teachplan.getCourseid();
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            // 如果父节点为空则获取父节点
            parentid = this.getTeachplanRoot(courseid);
        }

        // 取出父节点信息
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(parentid);
        if (!teachplanOptional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        // 父节点
        Teachplan teachplanParent = teachplanOptional.get();
        // 父节点级别
        String parentGrade = teachplanParent.getGrade();
        // 设置父节点
        Teachplan teachplanNew = new Teachplan();
        BeanUtils.copyProperties(teachplan, teachplanNew);
        teachplanNew.setParentid(parentid);
        teachplanNew.setStatus("0");// 未发布
        // 子节点的级别，根据父节点来判断
        if (parentGrade.equals("1")) {
            teachplanNew.setGrade("2");
        } else if (parentGrade.equals("2")) {
            teachplanNew.setGrade("3");
        }
        // 保存
        this.teachplanRepository.save(teachplanNew);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    public QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest) {
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        if (page <= 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 20;
        }
        // 设置分页参数
        PageHelper.startPage(page, size);
        // 分页查询
        Page<CourseInfo> courseListPage = this.courseMapper.findCourseListPage(courseListRequest);
        // 查询列表
        List<CourseInfo> list = courseListPage.getResult();
        // 总记录数
        long total = courseListPage.getTotal();
        // 查询结果集
        QueryResult<CourseInfo> queryResultQueryResult = new QueryResult<>();
        queryResultQueryResult.setList(list);
        queryResultQueryResult.setTotal(total);
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS, queryResultQueryResult);

    }

    @Transactional
    @Override
    public AddCourseResult addCourseBase(CourseBase courseBase) {
        // 课程状态默认为未发布
        courseBase.setStatus("202001");
        courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, courseBase.getId());
    }

    @Override
    public CourseBase getCourseBaseById(String courseId) {
        Optional<CourseBase> optional = this.courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            return courseBase;
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseResult updateCourseCourseBase(String id, CourseBase courseBase) {
        CourseBase one = this.getCourseBaseById(id);
        if (one == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        // 修改课程信息
        BeanUtils.copyProperties(courseBase, one);
        courseBaseRepository.save(one);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    @Override
    public CourseMarket getCourseMarketById(String courseId) {
        Optional<CourseMarket> optional = this.courseMarketRepository.findById(courseId);
        if (optional.isPresent()) {
            CourseMarket courseMarket = optional.get();
            return courseMarket;
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        CourseMarket one = this.getCourseMarketById(id);
        if (one == null) {
            one = new CourseMarket();
            one.setId(id);
        }
        BeanUtils.copyProperties(courseMarket, one);
        CourseMarket result = this.courseMarketRepository.save(one);
        if (result != null) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    // 保存课程图片
    @Override
    @Transactional
    public ResponseResult saveCoursePic(String courseId, String pic) {
        // 查询课程图片
        Optional<CoursePic> optional = this.coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if (optional.isPresent()) {
            coursePic = optional.get();
        }
        // 没有课程图片则新建对象
        if (coursePic == null) {
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        // 保存课程图片
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 查看课程图片
    @Override
    public CoursePic getCoursePic(String courseId) {
        if (courseId == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Optional<CoursePic> optional = this.coursePicRepository.findById(courseId);
        if (optional.isPresent()) {
            CoursePic coursePic = optional.get();
            return coursePic;
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseResult deleteCoursePic(String courseId) {
        if (courseId == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        long result = this.coursePicRepository.deleteByCourseid(courseId);
        if (result > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    // 课程视图查询
    @Override
    public CourseView getCourseView(String id) {
        CourseView courseView = new CourseView();
        // 查询课程基本信息
        Optional<CourseBase> optional = this.courseBaseRepository.findById(id);
        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            courseView.setCourseBase(courseBase);
        }

        // 查询课程图片信息
        Optional<CoursePic> optional1 = this.coursePicRepository.findById(id);
        if (optional.isPresent()) {
            CoursePic coursePic = optional1.get();
            courseView.setCoursePic(coursePic);
        }

        // 查询课程营销信息
        Optional<CourseMarket> optional2 = this.courseMarketRepository.findById(id);
        if (optional2.isPresent()) {
            CourseMarket courseMarket = optional2.get();
            courseView.setCourseMarket(courseMarket);
        }

        // 查询课程教学计划信息
        TeachplanNode teachplanNode = this.teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }

    // 这里面相当于前面进行的先将数据手动提交到mongodb,然后使用pageUrl预览页面
    @Override
    public CoursePublishResult preview(String courseId) {

        CourseBase one = this.findCourseBaseById(courseId);

        // 发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        // 站点
        cmsPage.setSiteId(publish_siteId);  // 课程预览站点
        // 模版
        cmsPage.setTemplateId(publish_templateId);
        // 页面名称
        cmsPage.setPageName(courseId + ".html");
        // 页面别名
        cmsPage.setPageAliase(one.getName());
        // 页面访问路径
        cmsPage.setPageWebPath(publish_page_WebPath);
        // 页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_PhyscialPath);
        // 数据url
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        // 远程请求cms保存页面信息
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        // 页面id
        String pageId = cmsPageResult.getCmsPage().getPageId();
        // 页面url
        String pageUrl = publish_previewUrl + pageId;

        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    // 根据id查询课程基本信息
    @Override
    public CourseBase findCourseBaseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            return courseBase;
        }

        ExceptionCast.cast(CourseCode.COURSE_GET_NOEXISTS);
        return null;
    }

    // 同页面预览,发布课程
    @Override
    @Transactional  // mysql更改状态，涉及事务
    public CoursePublishResult publish(String courseId) {
        // 课程信息
        CourseBase one = this.findCourseBaseById(courseId);
        // 发布课程详情页面
        CmsPostPageResult cmsPostPageResult = this.publish_page(courseId);
        if (!cmsPostPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        // 更新课程状态
        CourseBase courseBase = this.saveCourseBasePubStatus(courseId);
        if (courseBase == null) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        // 课程索引
        // 先创建CoursePub对象，然后将CoursePub对象保存到数,mysql据库
        CoursePub coursePub = this.createCoursePub(courseId);
        // 向数据库保存课程索引信息
        CoursePub newCoursePub = this.saveCoursePub(courseId, coursePub);
        if (newCoursePub == null) {
            // 创建课程索引失败
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_CREATE_INDEX_ERROR);
        }
        // 保存课程计划媒资信息到索引表
        this.saveTeachplanMediaPub(courseId);
        // 课程缓存
        // 页面url
        String pageUrl = cmsPostPageResult.getPageUrl();
        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    // 保存媒资信息
    @Override
    @Transactional
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        if (teachplanMedia==null || StringUtils.isEmpty(teachplanMedia.getTeachplanId())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM); // 校验关键数据
        }
        // 课程计划
        String teachplanId = teachplanMedia.getTeachplanId();
        // 查询课程计划
        Optional<Teachplan> optional = this.teachplanRepository.findById(teachplanId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_ISNULL);
        }
        Teachplan teachplan = optional.get();
        // 只允许为叶子节点选择视频
        String grade = teachplan.getGrade();
        if (StringUtils.isEmpty(grade) || !grade.equals("3")) {
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }

        TeachplanMedia one = null;
        Optional<TeachplanMedia> optional1 = this.teachplanMediaRepositroy.findById(teachplanId);
        if (!optional1.isPresent()) {
            one = new TeachplanMedia();
        }else{
            one = optional1.get();
        }

        //保存媒资与课程计划信息
        one.setTeachplanId(teachplanId);
        one.setCourseId(teachplanMedia.getCourseId());
        one.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        one.setMediaId(teachplanMedia.getMediaId());
        one.setMediaUrl(teachplanMedia.getMediaUrl());
        this.teachplanMediaRepositroy.save(one);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 更新课程发布状态
    private CourseBase saveCourseBasePubStatus(String courseId) {
        CourseBase courseBase = this.findCourseBaseById(courseId);
        // 更新发布状态
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        return save;
    }

    // 发布课程正式页面
    private CmsPostPageResult publish_page(String courseId) {
        CourseBase one = this.findCourseBaseById(courseId);
        // 发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);
        cmsPage.setPagePhysicalPath(publish_page_PhyscialPath);  // 页面最终存放在服务器中的位置
        cmsPage.setPageWebPath(publish_page_WebPath);
        cmsPage.setDataUrl(publish_dataUrlPre + courseId);
        cmsPage.setTemplateId(publish_templateId);
        cmsPage.setPageName(courseId + ".html");
        cmsPage.setPageAliase(one.getName());

        // 发布页面
        CmsPostPageResult cmsPostPageResult = this.cmsPageClient.postPageQuick(cmsPage);
        return cmsPostPageResult;
    }

    // 获取课程根节点，如果没有则添加根节点 返回节点id
    public String getTeachplanRoot(String courseId) {
        Optional<CourseBase> optional = this.courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        CourseBase courseBase = optional.get();
        // 取出课程计划根节点
        List<Teachplan> teachplanList = this.teachplanRepository.findByCourseidAndParentid(courseId, "0 ");
        if (teachplanList == null || teachplanList.size() <= 0) {
            // 新增一个根节点
            Teachplan teachplanRoot = new Teachplan();
            teachplanRoot.setCourseid(courseId);
            teachplanRoot.setPname(courseBase.getName());
            teachplanRoot.setParentid("0");
            teachplanRoot.setGrade("1");
            teachplanRoot.setStatus("0");
            this.teachplanRepository.save(teachplanRoot);
            return teachplanRoot.getId();
        }
        Teachplan teachplan = teachplanList.get(0);  // 如果是一级节点有一个，如果是二级三级节点有多个
        return teachplan.getId();
    }

    // 保存CoursePub,传参时重要内容的id，虽说对象有，但是定义出来更好
    public CoursePub saveCoursePub(String id, CoursePub coursePub) {
        // 根据课程id查询CoursePub,有则更新，无则添加
        CoursePub coursePubNew = null;
        Optional<CoursePub> optional = this.coursePubRepository.findById(id);
        if (optional.isPresent()) { // 有
            coursePubNew = optional.get();
        } else { // 没有
            coursePubNew = new CoursePub();
        }
        BeanUtils.copyProperties(coursePub, coursePubNew);
        coursePubNew.setId(id); // 原来有值后来copy后没有值，需要把传来的参数id设置到coursePubNew的id属性上
        coursePubNew.setTimestamp(new Date());    // 时间戳字段,logstash需要用到

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        coursePubNew.setPubTime(sdf.format(new Date())); // 发布时间，类型为字符串
        CoursePub save = this.coursePubRepository.save(coursePubNew);
        return save;
    }

    // 创建CoursePub对象
    private CoursePub createCoursePub(String courseId) {
        // 根据课程id查询这几张表的数据
        CoursePub coursePub = new CoursePub();
        coursePub.setId(courseId);

        // 1、根据课程id查询课程基本信息
        Optional<CourseBase> optional1 = courseBaseRepository.findById(courseId);
        if (optional1.isPresent()) {
            CourseBase courseBase = optional1.get();
            // 将courseBase中的数据拷贝到CoursePub中
            BeanUtils.copyProperties(courseBase, coursePub);
        }

        // 2、根据课程id查询课程营销信息
        Optional<CourseMarket> optional2 = courseMarketRepository.findById(courseId);
        if (optional2.isPresent()) {
            CourseMarket courseMarket = optional2.get();
            // 将courseBase中的数据拷贝到CoursePub中
            BeanUtils.copyProperties(courseMarket, coursePub);
        }

        // 3、根据课程id查询课程图片信息
        Optional<CoursePic> optional3 = coursePicRepository.findById(courseId);
        if (optional3.isPresent()) {
            CoursePic coursePic = optional3.get();
            // 将courseBase中的数据拷贝到CoursePub中
            BeanUtils.copyProperties(coursePic, coursePub);
        }
        // 4、根据课程id查询课程信息
        TeachplanNode teachplanNode = this.teachplanMapper.selectList(courseId);
        String teachplan = JSON.toJSONString(teachplanNode);
        coursePub.setTeachplan(teachplan);

        return coursePub;
    }
    
    // 保存课程计划媒资信息,先删除mediapub中指定id的数据,然后将查到的media数据保存到mediapub
    private void saveTeachplanMediaPub(String courseId){
        // 查询课程媒资信息
        List<TeachplanMedia> teachplanMediaList = this.teachplanMediaRepositroy.findByCourseId(courseId);
        // 将课程计划媒资信息存储到索引表
        this.teachplanMediaPubRepositroy.deleteByCourseId(courseId);
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMediaList) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            // 添加时间戳
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubList.add(teachplanMediaPub);
        }
        this.teachplanMediaPubRepositroy.saveAll(teachplanMediaPubList);
    }
}



