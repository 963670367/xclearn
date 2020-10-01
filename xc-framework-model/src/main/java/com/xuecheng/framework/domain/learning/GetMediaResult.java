package com.xuecheng.framework.domain.learning;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor  // 远程调用，需要使用无参构造方法
public class GetMediaResult extends ResponseResult {
    public GetMediaResult(ResultCode resultCode,String fileUrl) {
        super(resultCode);
        this.fileUrl = fileUrl;
    }
    // 媒体文件播放地址
    private String fileUrl;
}
