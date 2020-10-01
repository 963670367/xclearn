package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MediaProcessTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(MediaProcessTask.class);

    // ffmpeg绝对路径
    @Value("${xc-service-manage-media.ffmpeg-path}")
    private String ffmpegPath;

    // 上传文件根目录
    @Value("${xc-service-manage-media.video-location}")
    private String serverPath;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    // 接收视频处理的消息进行视频处理
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg) throws IOException {
        Map msgMap = JSON.parseObject(msg, Map.class);
        LOGGER.info("receive media process task msg:{} ", msgMap);
        // 1、解析消息,得到媒资文件id
        String mediaId = (String) msgMap.get("mediaId");
        // 2、获取媒资文件信息
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if (!optional.isPresent()) {
//            ExceptionCast.cast(MediaCode.MEIDA_FILE_NOT_EXISTS);
            // 直接返回即可，监听消息的后续操作，我们不执行即可
            return;
        }
        MediaFile mediaFile = optional.get();
        // 媒资文件类型
        String fileType = mediaFile.getFileType();
        if (fileType == null || !fileType.equalsIgnoreCase("avi")) { // 只处理avi文件，其他文件不进行处理，但是能够存储到服务器
            mediaFile.setProcessStatus("303004"); // 处理状态为无需处理,前面没有此字段
            mediaFileRepository.save(mediaFile);
            return;
        } else {
            mediaFile.setProcessStatus("303001"); // 处理状态为处理中，因为我们马上要进行处理
            mediaFileRepository.save(mediaFile);
        }

        // 3、生成mp4
        // 要处理视频文件路径
        String video_path = serverPath + mediaFile.getFilePath() + mediaFile.getFileName();
        // 生成的mp4的文件名
        String mp4_name = mediaId + ".mp4";
        // 生成的mp4所在的路径
        String mp4folder_path = serverPath + mediaFile.getFilePath();
        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath, video_path, mp4_name, mp4folder_path);
        String result = videoUtil.generateMp4();
        if (result == null || !result.equals("success")) {
            // 操作失败写入处理日志
            mediaFile.setProcessStatus("303003"); // 处理状态为处理失败
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        // 生成m3u8
        // mp4视频文件目录
        video_path = serverPath + mediaFile.getFilePath() + mp4_name;
        // m3u8文件名称
        String m3u8_name = mediaId + ".m3u8";
        // m3u8文件所在的目录
        String m3u8folder_path = serverPath + mediaFile.getFilePath() + "hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpegPath, video_path, m3u8_name, m3u8folder_path);
        result = hlsVideoUtil.generateM3u8();
        if (result == null || !result.equals("success")) {
            // 操作失败写入处理日志
            mediaFile.setProcessStatus("303003"); // 处理状态为处理失败
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            return;
        }
        // 获取m3u8列表
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        // 更新处理状态为成功
        mediaFile.setProcessStatus("303002");
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        // m3u8文件url
        mediaFile.setFileUrl(mediaFile.getFilePath() + "hls/" + m3u8_name); // 播放视频相对路径
        mediaFileRepository.save(mediaFile);

    }

}
