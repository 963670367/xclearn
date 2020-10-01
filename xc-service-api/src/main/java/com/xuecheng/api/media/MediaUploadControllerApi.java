package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "媒资上传接口", description = "媒资上传接口，提供文件上传，文件处理等接口",tags={"媒资上传接口"})
public interface MediaUploadControllerApi {

    // 上传前准备工作
    @ApiOperation("文件上传注册")
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt);

    @ApiOperation("校验分块文件是否存在")
    @ApiImplicitParams({@ApiImplicitParam(name = "fileMd5", value = "文件校验码", dataType = "String"),
            @ApiImplicitParam(name = "chunk", value = "文件编号", dataType = "int"),
            @ApiImplicitParam(name = "chunkSize", value = "文件大小", dataType = "int")})
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize);

    //fileVal:"file",//文件上传域的name
    @ApiOperation("上传分块")
    public ResponseResult uploadchunk(MultipartFile file, Integer chunk, String fileMd5);

    @ApiOperation("合并分块")
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt);
}
