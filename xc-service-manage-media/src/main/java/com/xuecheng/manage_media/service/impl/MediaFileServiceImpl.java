package com.xuecheng.manage_media.service.impl;

import com.xuecheng.framework.domain.course.response.QueryResponseResult;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import com.xuecheng.manage_media.service.IMediaFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class MediaFileServiceImpl implements IMediaFileService {

    private static Logger logger = LoggerFactory.getLogger(MediaFileServiceImpl.class);

    @Autowired
    private MediaFileRepository mediaFileRepository;

    // 分页列表文件查询
    @Override
    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        // 查询条件
        MediaFile mediaFile = new MediaFile();
        if (queryMediaFileRequest == null) {
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        // 查询条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("tag", ExampleMatcher.GenericPropertyMatchers.contains()) // tag字段模糊匹配
                .withMatcher("fileOriginalName", ExampleMatcher.GenericPropertyMatchers.contains()) // 文件原始名称模糊匹配 contains()
                .withMatcher("processStatus", ExampleMatcher.GenericPropertyMatchers.exact());// 处理状态精确匹配（默认,可以不配置）
        // 查询条件对象
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getTag())) {
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getFileOriginalName())) {
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }
        if (StringUtils.isNotEmpty(queryMediaFileRequest.getProcessStatus())) {
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        // 定义example案例
        Example<MediaFile> ex = Example.of(mediaFile, exampleMatcher);
        // 分页参数
        if (page<=0) {
            page=1;
        }
        if (size<=0) {
            size=20;
        }
        page = page-1;
        Pageable pageable = new PageRequest(page, size);
        // 分页查询
        Page<MediaFile> all = mediaFileRepository.findAll(ex, pageable);

        QueryResult<MediaFile> mediaFileQueryResult = new QueryResult<>(); // 返回数据集
        mediaFileQueryResult.setList(all.getContent()); // 数据列表
        mediaFileQueryResult.setTotal(all.getTotalElements()); // 设置总记录数

        // 返回结果
        return new QueryResponseResult(CommonCode.SUCCESS,mediaFileQueryResult);
    }
}
