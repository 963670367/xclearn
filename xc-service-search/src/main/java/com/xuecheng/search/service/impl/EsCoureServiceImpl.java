package com.xuecheng.search.service.impl;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.course.response.QueryResponseResult;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.search.service.IEsCourseService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EsCoureServiceImpl implements IEsCourseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsCoureServiceImpl.class);

    // 课程索引配置
    @Value("${xuecheng.elasticsearch.course.index}")
    private String es_index;
    @Value("${xuecheng.elasticsearch.course.type}")
    private String es_type;
    @Value("${xuecheng.elasticsearch.course.source_field}")
    private String es_source_field;

    // 媒资索引配置
    @Value("${xuecheng.elasticsearch.media.index}")
    private String media_index;
    @Value("${xuecheng.elasticsearch.media.type}")
    private String media_type;
    @Value("${xuecheng.elasticsearch.media.source_field}")
    private String media_source_field;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 课程搜索
     *
     * @param page
     * @param size
     * @param courseSearchParam
     * @return
     */
    @Override
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        if (courseSearchParam==null) {
            CourseSearchParam courseSearchParam1 = new CourseSearchParam();
        }
        // 设置索引
        SearchRequest searchRequest = new SearchRequest(es_index);
        // 设置搜索类型
        searchRequest.types(es_type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // source源字段过滤
        String[] source_fields = es_source_field.split(",");
        searchSourceBuilder.fetchSource(source_fields,new String[]{});
        // 关键字搜索
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())) {
            // 匹配关键字
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "teachplan", "description")
                    .minimumShouldMatch("70%") // 设置匹配占比,比如要搜索3个词，搜索到2词，用比例设置
                    .field("name",10);// 提升另一个字段的Boost值，权重，3者中name的权重是其余二者10倍
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        // 过滤
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())) {
            // 根据一级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())) {
            // 根据二级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
            // 根据难度等级进行分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }
        // 分页
        if (page<=0) {
            page = 1;
        }
        if (size<=0) {
            size=12;
        }
        // 起始记录下标
        int start = (page-1)*size;
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(size);
        // 布尔查询
        searchSourceBuilder.query(boolQueryBuilder);

        // 高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>"); // 设置前缀
        highlightBuilder.postTags("</font>");  // 设置后缀
        // 设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        // 请求搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            // 执行搜索
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("xuecheng search error...{}",e.getMessage());
            return new QueryResponseResult<>(CommonCode.SUCCESS,new QueryResult<CoursePub>());
        }

        // 结果集处理
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        // 总记录数
        long totalHits = hits.getTotalHits();

        List<CoursePub> list = new ArrayList<>();

        for (SearchHit searchHit : searchHits) {
            CoursePub coursePub = new CoursePub();
            // 数据都拿到了，我们拿我们展示需要的内容
            // 取出source,源文档
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            // 取出id
            String id = (String) sourceAsMap.get("id");
            // 取出名称
            String name = (String) sourceAsMap.get("name");
            // 取出图片
            String pic = (String) sourceAsMap.get("pic");
            // 取出价格
            Float price = null;
            if (sourceAsMap.get("price")!=null) {
                price = Float.parseFloat((String) sourceAsMap.get("price"));
            }
            // 取出高亮字段内容
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            if (highlightFields!=null) {
                HighlightField nameField = highlightFields.get("name");
                if (nameField!=null) {
                    Text[] fragments = nameField.getFragments();
                    StringBuffer buffer = new StringBuffer();
                    for (Text fragment : fragments) {
                        buffer.append(fragment);
                    }
                    name = buffer.toString();
                }
            }
            Double price_old = null;
            if (sourceAsMap.get("price_old")!=null) {
                price_old = (Double) sourceAsMap.get("price_old");
            }
            coursePub.setId(id);
            coursePub.setPrice(price);
            coursePub.setName(name);
            coursePub.setPic(pic);
            coursePub.setPrice_old(price_old);

            list.add(coursePub);
        }

        QueryResult<CoursePub> queryResult = new QueryResult();
        queryResult.setTotal(totalHits);
        queryResult.setList(list);

        return new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);
    }

    /**
     * 根据id查询课程信息
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, CoursePub> getall(String id) {
        // 设置索引
        SearchRequest searchRequest = new SearchRequest(es_index);
        // 设置搜索类型
        searchRequest.types(es_type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 查询条件，根据课程id查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("id",id));
        // 取消source源字段过滤，查询所有字段
//        searchSourceBuilder.fetchSource(new String[]{"name","grade","charge","pic"},new String[]{});
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = null;
        try {
            // 执行搜索
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取搜索结果
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        HashMap<String,CoursePub> map = new HashMap<>();
        for (SearchHit hit : searchHits) {
//            String courseId = hit.getId();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String courseId = (String) sourceAsMap.get("id");
            String name = (String) sourceAsMap.get("name");
            String grade = (String) sourceAsMap.get("charge");
            String pic = (String) sourceAsMap.get("pic");
            String description = (String) sourceAsMap.get("description");
            String teachplan = (String) sourceAsMap.get("teachplan");

            CoursePub coursePub = new CoursePub();
            coursePub.setId(courseId);
            coursePub.setName(name);
            coursePub.setDescription(description);
            coursePub.setGrade(grade);
            coursePub.setPic(pic);
            coursePub.setTeachplan(teachplan);

            map.put(courseId,coursePub);
        }

        return map;
    }

    /**
     * 获取课程媒资信息
     *
     * @param teachplanIds
     * @return
     */
    @Override
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {
        // 设置索引
        SearchRequest searchRequest = new SearchRequest(media_index);
        // 设置搜索类型
        searchRequest.types(media_type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // source源字段过滤
        String[] source_fields = media_source_field.split(",");
        searchSourceBuilder.fetchSource(source_fields,new String[]{});
        // 查询条件，根据课程计划id查询，可传入多个id
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;

        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        try {
            // 执行搜索
            searchResponse = this.restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取搜索结果
        SearchHits hits = searchResponse.getHits();
        SearchHit[] hits1 = hits.getHits();
        long totalHits = hits.getTotalHits();
        for (SearchHit searchHit : hits1) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String courseid = (String) sourceAsMap.get("courseid");
            String media_id = (String) sourceAsMap.get("media_id");
            String media_url = (String) sourceAsMap.get("media_url");
            String teachplan_id = (String) sourceAsMap.get("teachplan_id");
            String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");

            teachplanMediaPub.setCourseId(courseid);
            teachplanMediaPub.setMediaId(media_id);
            teachplanMediaPub.setTeachplanId(teachplan_id);
            teachplanMediaPub.setMediaUrl(media_url);
            teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);

            // 将数据加入列表
            teachplanMediaPubList.add(teachplanMediaPub);
        }
        // 构建返回课程媒资信息对象
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        queryResult.setList(teachplanMediaPubList);
        queryResult.setTotal(totalHits);

        QueryResponseResult<TeachplanMediaPub> queryResponseResult = new QueryResponseResult<>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

}
