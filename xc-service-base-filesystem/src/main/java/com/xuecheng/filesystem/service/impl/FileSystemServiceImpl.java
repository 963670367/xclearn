package com.xuecheng.filesystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.filesystem.service.IFileSystemService;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FileSystemServiceImpl implements IFileSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemServiceImpl.class);

    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;

    @Autowired
    FileSystemRepository fileSystemRepository;

    // 上传文件
    // 先判断有没有有文件，把请求传过来的参数和上传文件返回的参数放到filesystem对象中，
    // 将fileid以及其他文件信息存储到mongodb
    @Override
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata) {
        if (multipartFile == null) {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        // 上传文件到fdfs
        String fileId = fdfs_upload(multipartFile);
        // 创建文件信息对象
        FileSystem fileSystem = new FileSystem();
        // 文件id
        fileSystem.setFileId(fileId);
        // 文件在文件系统中的路径
        fileSystem.setFilePath(fileId);
        // 标签
        fileSystem.setFiletag(filetag);
        // 业务标识
        fileSystem.setBusinesskey(businesskey);
        // 元数据
        if (!StringUtils.isEmpty(metadata)) {
            try {
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 名称
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        // 大小
        fileSystem.setFileSize(multipartFile.getSize());
        fileSystem.setFileType(multipartFile.getContentType());
        fileSystemRepository.save(fileSystem);

        return new UploadFileResult(CommonCode.SUCCESS, fileSystem);
    }

    // 上传文件到fdfs，返回文件id
    private String fdfs_upload(MultipartFile multipartFile) {
        try {
            // 加载fdfs配置
            initFdfsConfig();
            // 创建tracker client
            TrackerClient trackerClient = new TrackerClient();
            // 创建tracker server
            TrackerServer trackerServer = trackerClient.getConnection();
            // 获取storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            // 创建storage client
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storeStorage);
            // 上传文件
            // 文件字节
            byte[] bytes = multipartFile.getBytes();
            // 文件原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            // 文件拓展名
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            // 文件id
            String fileId = storageClient1.upload_file1(bytes, extName, null);
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    // 加载fdfs的配置
    private void initFdfsConfig() {
        try {
            // 初始化tracker服务地址，多个tracker中间以半角逗号分割
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            // 初始化系统文件出错
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }

    }
}
