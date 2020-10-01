package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryTemplateRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import com.xuecheng.manage_cms.service.ICmsTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class CmsTemplateServiceImpl implements ICmsTemplateService {

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;
    /**
     * 分页查询模版
     *
     * @param page
     * @param size
     * @param queryTemplateRequest
     * @return
     */
    @Override
    public QueryResponseResult findList(int page, int size, QueryTemplateRequest queryTemplateRequest) {
        if (queryTemplateRequest == null) {
            queryTemplateRequest = new QueryTemplateRequest();
        }
        // 条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        // 页面别名模糊查询，需要自定义字符串的匹配器实现模糊查询
        exampleMatcher = exampleMatcher.withMatcher("templateName", ExampleMatcher.GenericPropertyMatchers.contains());
        // 条件值
        CmsTemplate cmsTemplate = new CmsTemplate();

        // 站点id
        if (StringUtils.isNotEmpty(queryTemplateRequest.getTemplateFileId())) {
            cmsTemplate.setTemplateFileId(queryTemplateRequest.getTemplateFileId());
        }
        // 页面别名
        if (StringUtils.isNotEmpty(queryTemplateRequest.getTemplateName())) {
            cmsTemplate.setTemplateName(queryTemplateRequest.getTemplateName());
        }

        // 创建条件实例
        Example<CmsTemplate> example = Example.of(cmsTemplate, exampleMatcher);
        if (page <= 0) {
            page = 1;
        }
        page -= 1;
        if (size <= 0) {
            size = 0;
        }
        // 分页对象
        Pageable pageable = PageRequest.of(page, size);
        // 分页查询
//        Page<CmsPage> all = this.cmsPageRepository.findAll(pageable);
        Page<CmsTemplate> all = this.cmsTemplateRepository.findAll(example, pageable);
        QueryResult<CmsTemplate> cmsTemplateQueryResult = new QueryResult<CmsTemplate>();
        cmsTemplateQueryResult.setTotal(all.getTotalElements());
        cmsTemplateQueryResult.setList(all.getContent());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, cmsTemplateQueryResult);
    }
}
