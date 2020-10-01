package com.xuecheng.manage_cms.web.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.ICmsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {

    @Autowired
    private ICmsPageService cmsPageService;

    // 页面查询
    @Override
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {
//        // 暂时采用接口数据，测试接口是否能够正常运行
//        QueryResult queryResult = new QueryResult();
//        queryResult.setTotal(2);
//        // 静态数据列表
//        List list = new ArrayList();
//        CmsPage cmsPage = new CmsPage();
//        cmsPage.setPageName("测试页面");
//        list.add(cmsPage);
//        queryResult.setList(list);
//        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, queryResult);
        return this.cmsPageService.findList(page, size, queryPageRequest);
    }

    // 添加页面
    @Override
    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return cmsPageService.add(cmsPage);
    }


    @Override
    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return cmsPageService.save(cmsPage);
    }

    /**
     * 根据id查询页面
     * @param id
     * @return
     */
    @Override
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable(value = "id") String id) {
        return this.cmsPageService.findById(id);
    }

    /**
     * 根据id更新页面
     * @param id
     * @param cmsPage
     * @return
     */
    @Override
    @PutMapping("/edit/{id}")
    public CmsPageResult edit(@PathVariable(value = "id") String id,@RequestBody CmsPage cmsPage) {
        return this.cmsPageService.update(id,cmsPage);
    }

    /**
     * 根据id删除页面
     * @param id
     * @return
     */
    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable(value = "id") String id) {
        return this.cmsPageService.delete(id);
    }

    /**
     * 发布页面
     * @param pageId
     * @return
     */
    @Override
    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return cmsPageService.postPage(pageId);
    }

    /**
     * 一键发布页面
     * @param cmsPage
     * @return
     */
    @Override
    @PostMapping("/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {
        return cmsPageService.postPageQuick(cmsPage);
    }


}
