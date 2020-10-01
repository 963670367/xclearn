package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cms.CmsTemplateControllerApi;
import com.xuecheng.framework.domain.cms.request.QueryTemplateRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.manage_cms.service.ICmsTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cms/template")
public class CmsTemplateController implements CmsTemplateControllerApi {

    @Autowired
    private ICmsTemplateService cmsTemplateService;

    /**
     * 分页查询模版
     * @param page
     * @param size
     * @param queryTemplateRequest
     * @return
     */
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryTemplateRequest queryTemplateRequest) {
        return this.cmsTemplateService.findList(page,size,queryTemplateRequest);
    }
}
