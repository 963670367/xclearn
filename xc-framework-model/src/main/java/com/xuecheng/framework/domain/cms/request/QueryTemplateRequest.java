package com.xuecheng.framework.domain.cms.request;

import com.xuecheng.framework.model.request.RequestData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class QueryTemplateRequest extends RequestData {

    //模版ID
    @Id
    @ApiModelProperty("模版id")
    private String templateId;
    //模版名称
    @ApiModelProperty("模版名称")
    private String templateName;
    //模版文件Id
    @ApiModelProperty("模版文件Id")
    private String templateFileId;
}
