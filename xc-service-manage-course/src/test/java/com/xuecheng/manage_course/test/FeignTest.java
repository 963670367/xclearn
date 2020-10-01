package com.xuecheng.manage_course.test;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FeignTest {
    @Autowired
    CmsPageClient cmsPageClient;

    @Test
    public void testFeign(){
        // 通过服务id调用cms的查询页面接口
        CmsPage cmsPage = cmsPageClient.findById("5ae193170e66183ee06f71c5");
        System.out.println(cmsPage);
    }
}
