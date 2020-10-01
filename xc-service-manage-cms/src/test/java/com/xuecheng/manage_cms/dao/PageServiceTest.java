package com.xuecheng.manage_cms.dao;

import com.xuecheng.manage_cms.service.ICmsPageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {

    @Autowired
    private ICmsPageService cmsPageService;
    @Test
    public void testPageStatic(){
        String html_str = this.cmsPageService.getPageHtml("5f3baf63b9206f2778a3f91c");
        System.out.println(html_str);
    }
}
