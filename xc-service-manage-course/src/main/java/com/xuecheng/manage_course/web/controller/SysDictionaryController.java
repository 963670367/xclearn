package com.xuecheng.manage_course.web.controller;

import com.xuecheng.api.course.SysDictionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_course.service.ISysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys/dictionary")
public class SysDictionaryController implements SysDictionaryControllerApi {
    @Autowired
    private ISysDictionaryService sysDictionaryService;

    @Override
    @GetMapping("/get/{dtype}")
    public SysDictionary getByType(@PathVariable("dtype") String dtype) {
        return this.sysDictionaryService.findByDType(dtype);
    }
}
