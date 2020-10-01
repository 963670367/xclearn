package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "cms页面管理接口", description = "cms页面管理接口，提供页面增、删、改、查")
public interface CmsPageControllerApi {
    // 页面查询
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "size", value = "每页记录", required = true, paramType = "path", dataType = "int"),
    })
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    @ApiOperation("添加页面")
    public CmsPageResult add(CmsPage cmsPage);

    @ApiOperation(value = "保存页面",notes="每一次预览都要添加页面，存在的时候报错，正确做法，没有的情况下才添加，有了就不添加了")
    public CmsPageResult save(CmsPage cmsPage);

    @ApiOperation("通过ID查询页面")
    @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "String")
    public CmsPage findById(String id);

    @ApiOperation("修改页面")
    @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "String")
    public CmsPageResult edit(String id, CmsPage cmsPage);

    @ApiOperation("删除页面")
    @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "String")
    public ResponseResult delete(String id);

    @ApiOperation("发布页面")
    public ResponseResult post(String pageId);

    @ApiOperation("一键发布页面")
    public CmsPostPageResult postPageQuick(CmsPage cmsPage);


}
