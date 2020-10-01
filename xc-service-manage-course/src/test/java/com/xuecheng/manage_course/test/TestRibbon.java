package com.xuecheng.manage_course.test;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRibbon {
    @Autowired
    RestTemplate restTemplate;

    // 负载均衡调用
    @Test
    public void testRibbon(){
        // ribbon客户端从eureka中获取服务列表，appliction的名称,instances currently registered with eureka
        // 服务id
        String serviceId = "xc-service-manage-cms";
        for (int i = 0; i < 10; i++) {
            // 通过服务id调用
            ResponseEntity<CmsPage> forEntity = restTemplate.getForEntity("http://" + serviceId + "/cms/page/get/5ae193170e66183ee06f71c5", CmsPage.class);
            CmsPage cmsPage = forEntity.getBody();
            System.out.println(cmsPage);
        }
    }

}
