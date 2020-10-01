package com.xuecheng.filesystem.service;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

public interface IFileSystemService {
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata);
}
