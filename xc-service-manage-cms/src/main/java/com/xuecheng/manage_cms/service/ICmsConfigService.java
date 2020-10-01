package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;

public interface ICmsConfigService {

    /**
     * 根据id查询配置管理信息
     * @param id
     * @return
     */
    public CmsConfig getConfigById(String id);
}
