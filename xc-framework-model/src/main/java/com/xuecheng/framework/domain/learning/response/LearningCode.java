package com.xuecheng.framework.domain.learning.response;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.ResultCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;

@ToString
public enum LearningCode implements ResultCode {
    LEARNING_GETMEDIA_ERROR(false,22009,"获取视频播放地址失败"),
    CHOOSECOURSE_USERISNULL(false,22010,"选择课程用户id为空"),
    CHOOSECOUESE_TASKISNULL(false,22011,"选择课程选课id为空");

    //操作代码
    @ApiModelProperty(value = "学习微服务操作是否成功", example = "true", required = true)
    boolean success;

    //操作代码
    @ApiModelProperty(value = "学习微服务操作代码", example = "22009", required = true)
    int code;
    //提示信息
    @ApiModelProperty(value = "学习微服务操作提示", example = "获取视频播放地址失败！", required = true)
    String message;

    private LearningCode(boolean success,int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }
    private static final ImmutableMap<Integer, LearningCode> CACHE;

    static {
        final ImmutableMap.Builder<Integer, LearningCode> builder = ImmutableMap.builder();
        for (LearningCode commonCode : values()) {
            builder.put(commonCode.code(), commonCode);
        }
        CACHE = builder.build();
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
