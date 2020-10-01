package com.xuecheng.manage_course.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// 参考Swagger文档定义FeignClient，注意接口的Url、请求参数类型、返回值类型与Swagger接口一致。
@FeignClient(value = "xc-service-manage-cms")  // 指定远程调用的服务名，com.xuecheng.manage_course.test.TestRibbon
public interface CmsPageClient {

    @GetMapping("/cms/page/get/{id}")
    public CmsPage findById(@PathVariable("id") String id);

    // 保存页面
    @PostMapping("/cms/page/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage);

    // 一键发布页面
    @PostMapping("/cms/page/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage);
}
