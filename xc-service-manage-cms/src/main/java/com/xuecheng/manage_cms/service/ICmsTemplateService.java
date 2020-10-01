package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.request.QueryTemplateRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;

public interface ICmsTemplateService {
    /**
     * 分页查询模版
     * @param page
     * @param size
     * @param queryTemplateRequest
     * @return
     */
    QueryResponseResult findList(int page, int size, QueryTemplateRequest queryTemplateRequest);
}
