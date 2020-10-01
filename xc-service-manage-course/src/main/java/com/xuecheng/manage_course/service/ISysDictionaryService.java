package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.system.SysDictionary;

public interface ISysDictionaryService {
    // 根据字典分类查询字典信息
    public SysDictionary findByDType(String dtype);
}
