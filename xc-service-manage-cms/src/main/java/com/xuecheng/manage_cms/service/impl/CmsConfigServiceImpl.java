package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.service.ICmsConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CmsConfigServiceImpl implements ICmsConfigService {
    @Autowired
    private CmsConfigRepository cmsConfigRepository;

    /**
     * 根据id查询配置管理信息
     *
     * @param id
     * @return
     */
    @Override
    public CmsConfig getConfigById(String id) {
        Optional<CmsConfig> optional = this.cmsConfigRepository.findById(id);
        if (optional.isPresent()) {
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return null;
    }
}
