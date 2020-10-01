package com.xuecheng.manage_course.service.impl;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.dao.SysDictionaryDao;
import com.xuecheng.manage_course.service.ISysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysDictionaryServiceImpl implements ISysDictionaryService {

    @Autowired
    private SysDictionaryDao sysDictionaryDao;

    // 根据字典分类查询字典信息
    @Override
    public SysDictionary findByDType(String dtype) {
        return this.sysDictionaryDao.findByDType(dtype);
    }
}
