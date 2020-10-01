package com.xuecheng.manage_cms.service.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import com.xuecheng.manage_cms.service.ICmsPageService;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CmsPageServiceImpl implements ICmsPageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 页面查询方法
     *
     * @param page
     * @param size
     * @param queryPageRequest
     * @return
     */
    @Override
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }
        // 条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        // 页面别名模糊查询，需要自定义字符串的匹配器实现模糊查询
        exampleMatcher = exampleMatcher.withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        // 条件值
        CmsPage cmsPage = new CmsPage();

        // 站点id
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        // 页面别名
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        // 创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        if (page <= 0) {
            page = 1;
        }
        page -= 1;
        if (size <= 0) {
            size = 0;
        }
        // 分页对象
        Pageable pageable = PageRequest.of(page, size);
        // 分页查询
//        Page<CmsPage> all = this.cmsPageRepository.findAll(pageable);
        Page<CmsPage> all = this.cmsPageRepository.findAll(example, pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
        cmsPageQueryResult.setTotal(all.getTotalElements());
        cmsPageQueryResult.setList(all.getContent());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
    }

    /**
     * 添加页面
     *
     * @param cmsPage
     * @return
     */
    @Override
    public CmsPageResult add(CmsPage cmsPage) {

        // 校验页面是否存在，根据页面名称、页面id、页面webpath进行查询
        CmsPage cp = this.cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),
                cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cp != null) {
            // 页面已经存在
            // 抛出异常，异常内容就是页面已经存在
//            throw new CustomException(CommonCode.FAIL);
//            ExceptionCast.cast(CommonCode.FAIL);
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);  // 页面名称已存在 此枚举实现了ResultCode
        }

//        if (cp==null){
        cmsPage.setPageId(null);  // 添加页面主键由spring data生成
        this.cmsPageRepository.save(cmsPage);
        // 返回结果
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
//        }

//        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 根据id查询页面
     *
     * @param id
     * @return
     */
    @Override
    public CmsPage findById(String id) {
        Optional<CmsPage> optional = this.cmsPageRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }

        return null;
    }

    /**
     * 根据id保存页面
     *
     * @param id
     * @param cmsPage
     * @return
     */
    @Override
    public CmsPageResult update(String id, CmsPage cmsPage) {
        // 根据id查询页面
        CmsPage one = this.findById(id);
        if (one != null) {
            // 更新模版id
            one.setTemplateId(cmsPage.getTemplateId());
            // 更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            // 更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            // 更新页面名称
            one.setPageName(cmsPage.getPageName());
            // 更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            // 更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            // 更新dataUrl
            one.setDataUrl(cmsPage.getDataUrl());
            CmsPage save = this.cmsPageRepository.save(one);
            if (save != null) {
                return new CmsPageResult(CommonCode.SUCCESS, save);
            }
        }
        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 根据id删除页面
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult delete(String id) {
        CmsPage cm = this.findById(id);
        if (cm != null) {
            this.cmsPageRepository.deleteById(id);
            return ResponseResult.SUCCESS();
        }
        return ResponseResult.FAIL();
    }

    // 页面静态化
    public String getPageHtml(String pageId) {
        // 获取页面模型数据
        Map model = this.getModelByPageId(pageId);
        if (model == null) {
            // 数据模型获取不到
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        // 获取页面模版
        String template = this.getTemplateByPageId(pageId);
        if (template == null) {
            // 页面模版为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        // 执行静态化
        String html = this.generateHtml(template, model);
        if (StringUtils.isEmpty(html)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    /*
    1.	静态化程序获取页面的DataUrl
    2.	静态化程序远程请求DataUrl获取数据模型
    3.	静态化程序获取页面的模版信息
    4.	执行页面静态化
    */
    // 页面静态化
    private String generateHtml(String template, Map model) {
        try {
            // 生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());
            // 模版加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", template);
            // 配置模版加载器
            configuration.setTemplateLoader(stringTemplateLoader);
            // 获取模版
            Template content = configuration.getTemplate("template");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(content, model);
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 获取页面模型数据
    private Map getModelByPageId(String pageId) {
        // 取出页面的信息
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage == null) {
            // 页面找不到
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        // 取出页面的url
        String dataUrl = cmsPage.getDataUrl();
        if (StringUtils.isEmpty(dataUrl)) {
            // 页面dataUrl为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        // 通过restTemplate请求url,获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }

    // 获取页面模版
    private String getTemplateByPageId(String pageId) {
        // 取出页面的信息
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage == null) {
            // 页面找不到
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        // 取出页面的templateId
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        // 根据id查询模版信息
        Optional<CmsTemplate> optional = this.cmsTemplateRepository.findById(templateId);
        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();
            // 获取模版文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            // 从GridFS中取模版文件内容
            // 根据id查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            // 打开下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            // 创建gridFsResources，用于获取对象
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            // 获取流中的数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 页面发布
    @Override
    public ResponseResult postPage(String pageId) {
        // 执行静态化
        String pageHtml = this.getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        // 保存静态化文件到gridFS中
        CmsPage cmsPage = this.saveHtml(pageId, pageHtml);
        // 向MQ发布消息
        this.sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 保存页面
     *
     * @param cmsPage
     * @return
     */
    @Override
    public CmsPageResult save(CmsPage cmsPage) {
        // 添加页面，根据页面名称、站点id、页面webpath查询
        CmsPage cmsPage1 = this.cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(),
                cmsPage.getPageName(), cmsPage.getPageWebPath());
        if (cmsPage1 != null) {
            // 更新
            return this.update(cmsPage1.getPageId(), cmsPage);
        } else {
            // 添加
            return this.add(cmsPage);
        }
    }

    @Override
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {

        // 添加页面
        CmsPageResult cmsPageResult = this.save(cmsPage);
        if (!cmsPageResult.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        // 要发布的页面id
        String pageId = cmsPage1.getPageId();
        // 发布页面
        ResponseResult responseResult = this.postPage(pageId);
        if (!responseResult.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }
        // 得到页面url
        // 页面url = 站点域名+站点webpath+页面webpath+页面名称
        String siteId = cmsPage1.getSiteId();
        Optional<CmsSite> optional = this.cmsSiteRepository.findById(siteId);
        if (optional.isPresent()) {
            CmsSite cmsSite = optional.get();
            String siteDomain = cmsSite.getSiteDomain();
            String siteWebPath = cmsSite.getSiteWebPath();
            String pageWebPath = cmsPage1.getPageWebPath();
            String pageName = cmsPage1.getPageName();
            String pageUrl = siteDomain + "" + pageWebPath + pageName;
            return new CmsPostPageResult(CommonCode.SUCCESS, pageUrl);
        }
        return new CmsPostPageResult(CommonCode.FAIL, null);
    }

    // 根据站点id查询站点信息
    @Override
    public CmsSite findCmsSiteById(String siteId) {
        Optional<CmsSite> optional = this.cmsSiteRepository.findById(siteId);
        if (optional.isPresent()) {
            CmsSite cmsSite = optional.get();
            return cmsSite;
        }
        return null;
    }

    // 发送页面发布消息

    /**
     * 先获取cmsPage,将pageId存储到map,然后转为json对象，使用siteId作为routingKey发送到MQ
     *
     * @param pageId
     */
    private void sendPostPage(String pageId) {
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        HashMap<String, String> msgMap = new HashMap<>();
        msgMap.put("pageId", pageId);
        // 消息内容
        String msg = JSON.toJSONString(msgMap);
        // 获取站点id作为routingKey
        String siteId = cmsPage.getSiteId();
        // 发布消息,指定交换机、routingKey、消息
        this.rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, msg);

    }

    // 保存页面静态内容

    /**
     * 先看看GridFS中有没有静态化内容，有则删除，
     * 没有则将content,用pageName进行标记放入流后存储到GridFS，
     * 将保存后返回的objectId存储到cmsPage的的htmlFileId字段
     *
     * @param pageId
     * @param content
     * @return
     */
    private CmsPage saveHtml(String pageId, String content) {

        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CmsPage cmsPage = optional.get();
        // 存储之前先删除
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        ObjectId objectId = null;
        try {
            // 保存html文件到GridFS
            InputStream inputStream = IOUtils.toInputStream(content, "utf-8");
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将文件存储到cmspage中
        cmsPage.setHtmlFileId(objectId.toHexString());
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }
}
