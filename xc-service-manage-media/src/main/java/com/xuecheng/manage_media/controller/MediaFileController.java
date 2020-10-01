package com.xuecheng.manage_media.controller;

import com.xuecheng.api.media.MediaFileControllerApi;
import com.xuecheng.framework.domain.course.response.QueryResponseResult;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.manage_media.service.IMediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/media/file")
public class MediaFileController implements MediaFileControllerApi {

    @Autowired
    private IMediaFileService mediaFileService;

    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,@PathVariable("size") int size, QueryMediaFileRequest queryMediaFileRequest) {
        // 媒资文件查询
        return this.mediaFileService.findList(page,size,queryMediaFileRequest);
    }
}