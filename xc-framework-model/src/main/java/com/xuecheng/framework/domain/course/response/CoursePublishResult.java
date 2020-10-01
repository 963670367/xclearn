package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;

@Data
public class CoursePublishResult extends ResponseResult {
    String previewUrl; // 页面预览的url，必须等到页面id才能拼装

    public CoursePublishResult(ResultCode resultCode, String previewUrl) {
        super(resultCode);
        this.previewUrl = previewUrl;
    }
}
