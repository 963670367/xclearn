package com.xuecheng.filesystem.controller;

import com.xuecheng.api.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.IFileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {

    @Autowired
    private IFileSystemService fileSystemService;

    /**
     * 上传文件
     *
     * @param multipartFile 文件
     * @param filetag       文件标签
     * @param businesskey   业务key
     * @param metadata      元信息，json格式
     * @return
     */
    @Override
    @PostMapping("/upload")
    public UploadFileResult upload(@RequestParam("multipartFile") MultipartFile multipartFile,
                                   @RequestParam(value = "filetag", required = true) String filetag,
                                   @RequestParam(value = "businesskey", required = false) String businesskey,
                                   @RequestParam(value = "metadata", required = false) String metadata) {
        return this.fileSystemService.upload(multipartFile, filetag, businesskey, metadata);
    }



}
