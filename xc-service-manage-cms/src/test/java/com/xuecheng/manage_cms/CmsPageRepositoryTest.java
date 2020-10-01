package com.xuecheng.manage_cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {
    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private RestTemplate restTemplate;
    // 分页查询测试
    @Test
    public void testFindPage(){
        int page = 0; // 从0开始
        int size = 10; // 每页记录数
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }
    // 测试查询全部
    @Test
    public void testFindAll(){
        List<CmsPage> all = this.cmsPageRepository.findAll();
        all.forEach(cmsPage-> System.out.println(cmsPage));

    }
    // 添加
    @Test
    public void testInsert(){
        // 定义实体类
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("s02");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }

    // 删除
    @Test
    public void testDelete(){
        cmsPageRepository.deleteById("5f1e8afb0336f73958d06f85");
    }

    // 更新
    @Test
    public void testUpdate(){
        Optional<CmsPage> optional = cmsPageRepository.findById("5f1e8aa80336f72f0c9f058f");
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            cmsPage.setPageName("测试页面哒哒哒");
            cmsPage = cmsPageRepository.save(cmsPage);
            System.out.println(cmsPage);// 修改对象后会把对象返回
        }
    }

    // 根据站点和页面类型分页查询
    @Test
    public void testFindBySiteIdAndPageType(){
        int page = 0; // 从0开始
        int size = 10; // 每页记录数
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> cmsPages = this.cmsPageRepository.findBySiteIdAndPageType("5a751fab6abb5044e0d19ea1", "1", pageable);
        System.out.println(cmsPages);
    }
    // 自定义条件查询
    @Test
    public void testFindAllByExample(){
        // 条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        // 页面别名模糊查询，需要自定义字符串的匹配器实现模糊查询
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        // 条件值
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");
        cmsPage.setPageAliase("课程");
//        cmsPage.setTemplateId();
        //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage,exampleMatcher);
        Pageable pageable =  PageRequest.of(0,10);
        Page<CmsPage> all = this.cmsPageRepository.findAll(example,pageable);
        for(CmsPage cmsPage1: all.getContent()){
            System.out.println(cmsPage1);
        }
    }

    @Test
    public void testRestTemplate(){
        ResponseEntity<Map> forEntity = this.restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        System.out.println(forEntity.getBody());
    }
}
