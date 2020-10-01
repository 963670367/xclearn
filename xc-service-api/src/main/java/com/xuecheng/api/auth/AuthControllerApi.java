package com.xuecheng.api.auth;

import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="授权登录",description = "授权登录、注销")
public interface AuthControllerApi {

    @ApiOperation("登录")
    public LoginResult login(LoginRequest loginRequest);

    @ApiOperation("注销")
    public ResponseResult logout();

}
