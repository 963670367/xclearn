package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.ICmsPageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Controller  // 显示html数据而不是json数据
// 将html数据输出到浏览器
public class CmsPagePreviewController extends BaseController {
    @Autowired
    private ICmsPageService pageService;

    // 页面预览
    @RequestMapping(value = "/cms/preview/{pageId}", method = RequestMethod.GET)
    public void preview(@PathVariable("pageId") String pageId) {
        String pageHtml = pageService.getPageHtml(pageId);
        if (StringUtils.isNotEmpty(pageHtml)) {
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                response.setHeader("Content-type","text/html;charset=utf-8");
                outputStream.write(pageHtml.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
