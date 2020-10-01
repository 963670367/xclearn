package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QueryResponseResult<CourseInfo> extends ResponseResult {
    QueryResult queryResult;

    public QueryResponseResult(ResultCode resultCode, QueryResult queryResult){
        super(resultCode);
        this.queryResult = queryResult;
    }
}
