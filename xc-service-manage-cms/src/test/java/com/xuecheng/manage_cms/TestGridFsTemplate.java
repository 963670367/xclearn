package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestGridFsTemplate {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    // 存储文件
    @Test
    public void testGridFs() throws FileNotFoundException{
        // 要存储的文件
        File file = new File("D:\\course.ftl");
        // 定义输入流
        FileInputStream inputStream = new FileInputStream(file);
        // 向Grids存储文件
        ObjectId objectId = this.gridFsTemplate.store(inputStream,"course.ftl");
        // 得到文件ID
        String fileId = objectId.toString();
        System.out.println(fileId);
    }

    // 取文件
    @Test
    public void queryFile() throws IOException{
        String fileId = "5f37bbc8b9206f3bf04fe64f";
        // 根据id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        // 打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        // 创建gridFsResources，用于获取对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        // 获取流中的数据
        String s = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
        System.out.println(s);
    }

    // 删除文件
    @Test
    public void testDeleteFile() throws IOException{
        String fileId = "5f37bbc8b9206f3bf04fe64f";
        // 根据文件id删除fs.files和fs.chunks中的记录
        this.gridFsTemplate.delete(Query.query(Criteria.where("_id").is(fileId)));
    }
}
