package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.request.QueryTemplateRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value="cms模版管理接口",description = "cms模版管理接口")
public interface CmsTemplateControllerApi {
    // 页面查询
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value="页码",required = true,paramType = "path",dataType = "int"),
            @ApiImplicitParam(name="size",value="每页记录",required = true,paramType = "path",dataType = "int"),
    })
    public QueryResponseResult findList(int page, int size, QueryTemplateRequest queryTemplateRequest);

}
