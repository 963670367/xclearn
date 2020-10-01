package com.xuecheng.manage_media.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import com.xuecheng.manage_media.service.IMediaUploadService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
public class MediaUploadServiceImpl implements IMediaUploadService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final static Logger LOGGER = LoggerFactory.getLogger(MediaUploadServiceImpl.class);

    // 上传文件根目录
    @Value("${xc-service-manage-media.upload-location}")
    private String uploadLocation;
    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    private String routing_media_video;

    /**
     * 根据文件md5获得文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件拓展名
     * @param fileMd5 文件md5值
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    private String getFilePath(String fileMd5,String fileExt){
        String filePath = uploadLocation+fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+"."+fileExt;
        return filePath;
    }

    // 得到文件目录相对路径，路径中去掉根目录
    private String getFileFolderRelativePath(String fileMd5){
        String filePath = fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2)+"/"+fileMd5+"/";
        return filePath;
    }

    // 得到文件所在的目录
    private String getFileFolderPath(String fileMd5){
        String fileFolderPath = uploadLocation+fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2)+"/"+fileMd5+"/";
        return fileFolderPath;
    }

    // 创建文件目录
    private boolean createFileFold(String fileMd5){
        // 文件不存在时做一些准备工作，检查文件所在目录是否存在，如果不存在则创建
        // 创建上传文件目录
        String fileFolderPath = getFileFolderPath(fileMd5);
        File fileFolder = new File(fileFolderPath);
        if (!fileFolder.exists()) {
            // 创建文件夹
            boolean mkdirs = fileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }


    @Override
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        // 检查文件是否上传
        // 1.得到文件的路径
        String filePath = getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        // 2.查询数据库文件是否存在
        Optional<MediaFile> optional = this.mediaFileRepository.findById(fileMd5);
        // 文件存在直接返回
        if (file.exists() && optional.isPresent()) {
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        boolean fileFold = this.createFileFold(fileMd5);
        if(!fileFold){
            // 上传文件目录创建失败
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_CREATEFOLDER_FAIL);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    private String getChunkFileFolderPath(String fileMd5){
        String fileChunkFolderPath = this.getFileFolderPath(fileMd5)+"chunks"+"/";
        return fileChunkFolderPath;
    }

    @Override
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        // 得到文件所在的路径
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        // 块文件的名称以1,2,3...序号命名，没有扩展名
        File chunkFile = new File(chunkFileFolderPath + chunk);
        if (chunkFile.exists()) {
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK,true);
        }else{
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK,false);
        }
    }


    // 块文件上传
    @Override
    public ResponseResult uploadchunk(MultipartFile file, Integer chunk, String fileMd5) {
        if (file == null) {
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_EXIST);
        }
        // 创建块文件目录
        boolean fileFolder = this.createChunkFileFolder(fileMd5);
        if(!fileFolder){
            ExceptionCast.cast(MediaCode.UPLOAD_FILE_UPLOAD_CREATEFOLDER_FAIL);
        }
        // 块文件
        File chunkFile = new File(this.getChunkFileFolderPath(fileMd5) + chunk);
        // 上传的块文件
        InputStream inputStrem = null;
        FileOutputStream outputStream = null;

        try {
            inputStrem = file.getInputStream();
            outputStream = new FileOutputStream(chunkFile);
            IOUtils.copy(inputStrem,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("upload chunk file fail:{}",e.getMessage());
            ExceptionCast.cast(MediaCode.CHUNK_FILE_EXIST_CHECK);
        } finally {
            try {
                inputStrem.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 创建块文件目录
    private boolean createChunkFileFolder(String fileMd5) {
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        if (!chunkFileFolder.exists()) {
            // 创建文件夹
            boolean mkdirs = chunkFileFolder.mkdirs();
            return mkdirs;
        }
        return true;
    }

    // 合并分块
    @Override
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        // 获取块文件路径
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        File chunkfileFolder = new File(chunkFileFolderPath);
        if (!chunkfileFolder.exists()) {
            chunkfileFolder.mkdirs();
        }
        // 合并文件路径
        File mergeFile = new File(getFilePath(fileMd5, fileExt));
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        boolean newFile = false;
        try {
            newFile = mergeFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("mergechunks ... create merge file error{}",e.getMessage());
        }
        // 写在try后会被此catch块捕获到，不抛出到函数外面
        if (!newFile) {
            ExceptionCast.cast(MediaCode.MERGE_FILE_CREATEFAIL);
        }
        // 获取块文件，此列表已经时排好序的列表
        List<File> chunkFiles = this.getChunkFiles(chunkfileFolder);
        // 合并文件并返回
        mergeFile = mergeFile(mergeFile,chunkFiles);
        if(mergeFile==null){
            // 合并文件失败
            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        // 校验文件
        boolean checkResult = this.checkFileMd5(mergeFile,fileMd5);
        if (!checkResult) {
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        // 将文件信息保存在数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        mediaFile.setFileId(fileMd5);
        // 文件路径保存相对路径
        mediaFile.setFilePath(getFileFolderRelativePath(fileMd5));
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        // 状态为上传成功
        mediaFile.setFileStatus("301002");
        MediaFile save = this.mediaFileRepository.save(mediaFile);
        // 向MQ发送视频处理消息
        return this.sendProcessVideoMsg(save.getFileId());
    }

    // 发送视频处理消息
    public ResponseResult sendProcessVideoMsg(String mediaId) {
        // 向MQ发送视频处理消息
        Optional<MediaFile> optional = this.mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()) {
            return new ResponseResult(CommonCode.FAIL);
        }
        MediaFile mediaFile = optional.get();
        // 发送视频处理消息
        HashMap<String, String> msgMap = new HashMap<>();
        msgMap.put("mediaId",mediaId);
        // 发送的消息
        String msg = JSON.toJSONString(msgMap);
        try {
            this.rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routing_media_video,msg);
        } catch (AmqpException e) {
            // 消息发送失败
            return new ResponseResult(CommonCode.MSG_SEND_FAIL);
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 校验文件Md5
    private boolean checkFileMd5(File mergeFile, String fileMd5) {
        if ((mergeFile == null || StringUtils.isEmpty(fileMd5))) {
            return false;
        }
        // 进行md5校验
        FileInputStream mergeFileInputstream = null;
        try {
            // 创建文件输入流
            mergeFileInputstream = new FileInputStream(mergeFile);
            // 得到文件的md5
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileInputstream);
            // 比较md5
            if (mergeFileMd5.equalsIgnoreCase(fileMd5)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("checkFileMd5 error,file is:{},md5 is:{},",mergeFile.getAbsoluteFile(),fileMd5);
        }finally {
            try {
                mergeFileInputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 合并文件
    private File mergeFile(File mergeFile, List<File> chunkFiles) {
        try {
            // 创建文件写对象
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile,"rw");
            // 遍历分块文件开始合并
            // 读取文件缓存区
            byte[] bytes = new byte[1024];
            for (File chunkFile : chunkFiles) {
                RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"r");
                int len = -1;
                // 读取分块文件
                while ((len=raf_read.read(bytes))!=-1){
                    // 向合并文件中写数据
                    raf_write.write(bytes,0,len);
                }
                raf_read.close();
            }
            raf_write.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("merge file error:{}",e.getMessage());
            return null;
        }
        return mergeFile;
    }

    // 对文件块排序
    private List<File> getChunkFiles(File chunkfileFolder) {
        // 获取路径下的所有文件块文件
        File[] chunkFiles = chunkfileFolder.listFiles();
        // 将文件数组转为list，并排序
        List<File> fileList = new ArrayList<>(Arrays.asList(chunkFiles));
        Collections.sort(fileList,new Comparator<>() {
            @Override
            public int compare(File o1, File o2) {
                if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                    return -1; // 升序排列
                }
                return 1;
            }
        });
        return fileList;
    }
}
