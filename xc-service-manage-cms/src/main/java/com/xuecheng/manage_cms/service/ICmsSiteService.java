package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.QueryResponseResult;

public interface ICmsSiteService {
    /**
     * 列表查询方法
     *
     * @param cmsSite
     * @return
     */
    public QueryResponseResult findList(CmsSite cmsSite);

}
