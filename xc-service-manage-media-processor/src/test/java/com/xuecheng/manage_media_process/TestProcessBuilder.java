package com.xuecheng.manage_media_process;

import com.xuecheng.framework.utils.Mp4VideoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProcessBuilder {


    // 使用ProcessBuilder调用第三方程序
    @Test
    public void callThirdProgramer() throws IOException{
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("ping","127.0.0.1"); // 看成命令行即可，一个可以理解为每个参数为一条命令的的用空格隔开的内容

        // 将标准输入流和错误输入流合并，通过标准输入流读取信息。有ping IPAddr有正常输出和错误输出
        processBuilder.redirectErrorStream(true);
        try {
            // 启动进程
            Process start = processBuilder.start();
            // 获取输入流
            InputStream inputStream = start.getInputStream();
            // 转成字符输入流
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
            int len = -1;
            char[] c = new char[1024];
            StringBuffer outputString = new StringBuffer();
            // 读取进程输入流中的内容
            while ((len=inputStreamReader.read(c))!=-1){
                String s = new String(c, 0, len);
                outputString.append(s);
                System.out.println(s);
            }

            // 打印process builder环境信息
            Map<String, String> envArgs = processBuilder.environment();//Returns a string map view of this process builder's environment.
            for (Map.Entry<String, String> stringStringEntry : envArgs.entrySet()) {
                System.out.println(stringStringEntry.getKey()+":  "+stringStringEntry.getValue());
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // 测试FFmpeg
    @Test
    public void callFFmpeg() throws IOException{
        ProcessBuilder processBuilder = new ProcessBuilder();
        // 设置第三方程序的命令
        List<String> commondlist = new ArrayList<>();
        commondlist.add("C:/Program Files/ffmpeg-20200831-4a11a6f-win64-static/bin/ffmpeg.exe");
        commondlist.add("-i");
        commondlist.add("D:/develop/video/tcp.mp4");
        commondlist.add("-y"); // 覆盖输出文件
        commondlist.add("-c:v");
        commondlist.add("libx264");
        commondlist.add("-s");
        commondlist.add("1280x720");
        commondlist.add("-pix_fmt");
        commondlist.add("yuv420p");
        commondlist.add("-b:a");
        commondlist.add("63k");
        commondlist.add("-b:v");
        commondlist.add("753k");
        commondlist.add("-r");
        commondlist.add("18");
        commondlist.add("D:/develop/ffmpeg/1.avi");
        processBuilder.command(commondlist); // 看成命令行即可，一个可以理解为每个参数为一条命令的的用空格隔开的内容

        // 将标准输入流和错误输入流合并，通过标准输入流读取信息。有ping IPAddr有正常输出和错误输出
        processBuilder.redirectErrorStream(true);
        try {
            // 启动进程
            Process start = processBuilder.start();
            // 获取输入流
            InputStream inputStream = start.getInputStream();
            // 转成字符输入流
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
            int len = -1;
            char[] c = new char[1024];
            StringBuffer outputString = new StringBuffer();
            // 读取进程输入流中的内容
            while ((len=inputStreamReader.read(c))!=-1){
                String s = new String(c, 0, len);
                outputString.append(s);
                System.out.println(s);
            }

            // 打印process builder环境信息
            Map<String, String> envArgs = processBuilder.environment();//Returns a string map view of this process builder's environment.
            for (Map.Entry<String, String> stringStringEntry : envArgs.entrySet()) {
                System.out.println(stringStringEntry.getKey()+":  "+stringStringEntry.getValue());
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //测试使用工具类将avi转成mp4
    @Test
    public void testProcessMp4(){
        //String ffmpeg_path, String video_path, String mp4_name, String mp4folder_path
        //ffmpeg的路径
        String ffmpeg_path = "C:/Program Files/ffmpeg-20200831-4a11a6f-win64-static/bin/ffmpeg.exe";
        //video_path视频地址
        String video_path = "D:/develop/video/tcp.mp4";
        //mp4_name mp4文件名称
        String mp4_name  ="1.flv";
        //mp4folder_path mp4文件目录路径
        String mp4folder_path="D:/develop/ffmpeg/";
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_path);
        //开始编码,如果成功返回success，否则返回输出的日志
        String result = mp4VideoUtil.generateMp4();
        System.out.println(result);
    }

}
