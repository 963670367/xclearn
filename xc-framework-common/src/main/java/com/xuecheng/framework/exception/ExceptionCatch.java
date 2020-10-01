package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice //控制器增强
public class ExceptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    // 定义map，配置异常信息所对应的代码,ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
    // 使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();

    static {
        //在这里加入一些基础的异常类型判断
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAM);
    }
    // 捕获CustomException此类异常
    @ResponseBody  // 不写报404异常
    @ExceptionHandler(CustomException.class) // 指定捕获哪一类异常

    public ResponseResult customException(CustomException customException) {
        // 记录日志
        LOGGER.error("catch Exceptin: {}" + customException.getMessage());
        ResultCode resultCode = customException.getResultCode();
        return new ResponseResult(resultCode);
    }

    // 捕获Exception此类异常
    @ResponseBody  // 不写报404异常
    @ExceptionHandler(Exception.class) // 指定捕获哪一类异常
    public ResponseResult exception(Exception exception) {
        // 记录日志
        LOGGER.error("catch Exceptin: {}" + exception.getMessage());
        if (EXCEPTIONS == null) {
            EXCEPTIONS = builder.build(); // EXCEPTIONS构建成功
        }
        // 从EXCEPTIONS中异常类型所对应的代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        if (resultCode!=null) {
            return new ResponseResult(resultCode);
        }else{
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }
    }

}
