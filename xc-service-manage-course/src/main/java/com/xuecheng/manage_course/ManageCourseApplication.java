package com.xuecheng.manage_course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Administrator
 * @version 1.0
 **/
@EnableDiscoveryClient  // 表示它是一个Eureka客户端
@EnableFeignClients  // Spring会扫描标记了@FeignClient注解的接口，并生成此接口的代理对象
@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.course") // 扫描实体类
@ComponentScan(basePackages={"com.xuecheng.api"}) // 扫描接口
@ComponentScan(basePackages={"com.xuecheng.manage_course"})
@ComponentScan(basePackages={"com.xuecheng.framework"}) // 扫描common下的所有类
public class ManageCourseApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ManageCourseApplication.class, args);
    }


    // 删除RestTemplate：feign已经自动集成了Ribbon负载均衡的RestTemplate。所以，此处不需要再注册RestTemplate。
//    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate(){
//        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
//    }
}
