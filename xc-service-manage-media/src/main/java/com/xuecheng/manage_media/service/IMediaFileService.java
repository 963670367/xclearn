package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.course.response.QueryResponseResult;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;

public interface IMediaFileService {
    // 媒资文件查询
    QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest);
}
