package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.service.ICmsSiteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CmsSiteServiceImpl implements ICmsSiteService {

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    /**
     * 站点查询方法
     *
     * @param cmsSite
     * @return
     */
    @Override
    public QueryResponseResult findList(CmsSite cmsSite) {
        if (cmsSite == null) {
            cmsSite = new CmsSite();
        }
        // 条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        // 条件值
        CmsSite cmsite1 = new CmsSite();

        // 站点id
        if (StringUtils.isNotEmpty(cmsSite.getSiteId())) {
            cmsite1.setSiteId(cmsSite.getSiteId());
        }

        // 创建条件实例
        Example<CmsSite> example = Example.of(cmsite1, exampleMatcher);

        // 分页查询
//        Page<CmsPage> all = this.cmsPageRepository.findAll(pageable);
        List<CmsSite> all = this.cmsSiteRepository.findAll(example);
        QueryResult<CmsSite> cmsSiteQueryResult = new QueryResult<>();
        cmsSiteQueryResult.setTotal(all.size());
        cmsSiteQueryResult.setList(all);
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, cmsSiteQueryResult);
    }
}
