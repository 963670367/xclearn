package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {

    @Autowired
    RestHighLevelClient client;

    @Autowired
    RestClient restClient;

    // 删除索引库
    @Test
    public void testDeleteIndex() throws IOException {
        // 删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");
        // 删除索引
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);
        // 删除索引响应结果
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }

    // 创建索引库
    @Test
    public void testCreateIndex() throws IOException {
        // 创建索引请求对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");
        // 设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", 1).put("number_of_replicas", 0));
        // 设置映射
        createIndexRequest.mapping("doc", "{\n" +
                "    \"properties\":{\n" +
                "        \"name\":{\n" +
                "            \"type\":\"text\",\n" +
                "            \"analyzer\":\"ik_max_word\",\n" +
                "            \"search_analyzer\":\"ik_smart\"\n" +
                "        },\n" +
                "        \"description\":{\n" +
                "            \"type\":\"text\",\n" +
                "            \"analyzer\":\"ik_max_word\",\n" +
                "            \"search_analyzer\":\"ik_smart\"\n" +
                "        },\n" +
                "        \"pic\":{\n" +
                "            \"type\":\"text\",\n" +
                "            \"index\":false\n" +
                "        },\n" +
                "        \"studymodel\":{\n" +
                "            \"type\":\"keyword\"\n" +
                "        },\n" +
                "        \"price\":{\n" +
                "            \"type\":\"float\"\n" +
                "        },\n" +
                "        \"timestamp\":{\n" +
                "            \"type\":\"date\",\n" +
                "            \"format\":\"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\"\n" +
                "        }\n" +
                "    }\n" +
                "}", XContentType.JSON);
        // 创建索引操作客户端
        IndicesClient indices = client.indices();
        // 创建响应对象
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        // 得到响应结果
        boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
        System.out.println(shardsAcknowledged);
    }

    /*
    * 说明：增删改查操作，有多种选择方式，我们只选取id进行操作
    *
    * */

    // 添加文档
    @Test
    public void testAddDocument() throws IOException {
        // 准备json数据
        HashMap<Object, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud 实战");
        jsonMap.put("description", "Spring Cloud是一系列框架的有序集合。它利用Spring Boot的开发便利" +
                "性巧妙地简化了分布式系统基础设施的开发，如服务发现注册、配置中心、消息总线、负载均衡、断路器" +
                "、数据监控等，都可以用Spring Boot的开发风格做到一键启动和部署。");
        jsonMap.put("pic","group1/M00/00/01/wKhlQsdgsdf2345.jpg");
        jsonMap.put("studymodel","201002");
        jsonMap.put("price",38.6f);
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp",dataFormat.format(new Date()));
        // 索引请求对象
        IndexRequest indexRequest = new IndexRequest("xc_course","doc");
        // 指定索引文档内容
        indexRequest.source(jsonMap);
        // 索引响应对象
        IndexResponse indexResponse = client.index(indexRequest);
        // 获取响应结果
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println(result);
    }

    // 查询文档
    @Test
    public void testQueryDocument() throws IOException {
        // 索引请求对象
        GetRequest getRequest = new GetRequest("xc_course","doc","2");
        // 索引响应对象
        GetResponse getResponse = client.get(getRequest);
        // 获取响应结果
        boolean exists = getResponse.isExists();
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        System.out.println(sourceAsMap);
    }

    // 更新文档
    @Test
    public void updateDoc() throws IOException  {
        UpdateRequest updateRequest = new UpdateRequest("xc_course", "doc", "2");
        HashMap<Object, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud 实战");
        jsonMap.put("description", "Spring Cloud是一系列框架的有序集合。它利用Spring Boot的开发便利" +
                "性巧妙地简化了分布式系统基础设施的开发，如服务发现注册、配置中心、消息总线、负载均衡、断路器" +
                "、数据监控等，都可以用Spring Boot的开发风格做到一键启动和部署。");
        jsonMap.put("pic","group1/M00/00/01/wKhlQsdgsdf2345.jpg");
        jsonMap.put("studymodel","201002");
        jsonMap.put("price",38.6f);
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp",dataFormat.format(new Date()));
        updateRequest.doc(jsonMap);
        UpdateResponse update = client.update(updateRequest);
        RestStatus status = update.status();
        System.out.println(status);
    }

    // 删除文档
    @Test
    public void testDeleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("xc_course", "doc", "2");
        DeleteResponse deleteResponse = client.delete(deleteRequest);
        RestStatus status = deleteResponse.status();
        System.out.println(status);

    }



    // 分页查询
    @Test
    public void testPage() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        // 分页查询，设置起始下标，从0开始
        searchSourceBuilder.from(0);
        // 每页显示个数
        searchSourceBuilder.size(3);
        // source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);

    }

    // 全部查询
    @Test
    public void testFindAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        // source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit searchHit : searchHits) {
            String type = searchHit.getType();
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("name"));
            System.out.println(sourceAsMap.get("studymodel"));
            System.out.println(sourceAsMap.get("description"));
        }
    }

    // Term查询
    @Test
    public void testTermQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));
        // source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);


        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit searchHit : searchHits) {
            String type = searchHit.getType();
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("name"));
            System.out.println(sourceAsMap.get("studymodel"));
            System.out.println(sourceAsMap.get("description"));
        }
    }

    // 根据id精确查询
    @Test
    public void testIds() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] split = new String[]{"297e7c7c62b8aa9d0162b8ab13910000","4028e58161bcf7f40161bcf8b77c0000"};
        List<String> idList = Arrays.asList(split);
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",idList));
        // source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);


        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit searchHit : searchHits) {
            String type = searchHit.getType();
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("name"));
            System.out.println(sourceAsMap.get("studymodel"));
            System.out.println(sourceAsMap.get("description"));
        }
    }
    // 根据关键字搜索
    @Test
    public void testMatchQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        // 匹配关键字
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发").operator(Operator.OR));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);

        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit searchHit : searchHits) {
            String type = searchHit.getType();
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("name")+"   "+sourceAsMap.get("studymodel")+"  "+sourceAsMap.get("description"));
            System.out.println("------------------------------------------------------------");
        }
    }

    // 根据关键字匹配占比搜索
    @Test
    public void testMinimumShouldMatchQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        // 匹配关键字
        searchSourceBuilder.query(QueryBuilders.matchQuery("description","spring开发")
                .minimumShouldMatch("70%"));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);

        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit searchHit : searchHits) {
            String type = searchHit.getType();
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("name")+"   "+sourceAsMap.get("studymodel")+"  "+sourceAsMap.get("description"));
            System.out.println("------------------------------------------------------------");
        }
    }

    // 根据关键字匹配占比搜索
    @Test
    public void testMinimumShouldMatchQueryField() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        // 匹配关键字
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring框架","description","name")
                .minimumShouldMatch("70%").field("name",10));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);

        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit searchHit : searchHits) {
            String type = searchHit.getType();
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("name")+"   "+sourceAsMap.get("studymodel")+"  "+sourceAsMap.get("description"));
            System.out.println("------------------------------------------------------------");
        }
    }

    // 布尔查询
    @Test
    public void testBoolQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        // 匹配关键字
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring框架", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);
        // TermQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQueryBuilder);
        boolQueryBuilder.must(multiMatchQueryBuilder);

        // 设置布尔查询对象
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);

        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit searchHit : searchHits) {
            String type = searchHit.getType();
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("name")+"   "+sourceAsMap.get("studymodel")+"  "+sourceAsMap.get("description"));
            System.out.println("------------------------------------------------------------");
        }
    }

    // 过滤器
    @Test
    public void testFilterQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("xc_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // source源字段过滤
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","description"},new String[]{});
        searchRequest.source(searchSourceBuilder);
        // 匹配关键字
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring框架", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);
        // 布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        // 设置布尔查询对象
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);

        SearchHits searchHits = searchResponse.getHits();
        for (SearchHit searchHit : searchHits) {
            String type = searchHit.getType();
            String index = searchHit.getIndex();
            String id = searchHit.getId();
            float score = searchHit.getScore();
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            System.out.println(sourceAsMap.get("name")+"   "+sourceAsMap.get("studymodel")+"  "+sourceAsMap.get("description"));
            System.out.println("------------------------------------------------------------");
        }
    }
}