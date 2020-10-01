package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

// String 为 主键@Id 对应属性类型
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
