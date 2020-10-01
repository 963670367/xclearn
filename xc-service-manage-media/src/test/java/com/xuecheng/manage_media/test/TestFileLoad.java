package com.xuecheng.manage_media.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFileLoad {
    // 测试文件分块方法
    @Test
    public void testChunk() throws IOException {
        // 源文件
        File sourceFile = new File("D:/develop/video/tcp.mp4");
        // 分片目录，不存在则创建
        String chunkPath = "D:/develop/ffmpeg/chunk/";
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        // 分块大小
        long chunkSize = 1024 * 1024 * 1;
        // 分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        if (chunkNum <= 0) {
            chunkNum = 1;
        }
        // 缓存区大小
        byte[] b = new byte[1024];
        // 使用RandomAccessFile访问文件,随机读文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        // 分块
        for (long i = 0; i < chunkNum; i++) {
            // 创建分块文件,在分块目录下以数字为编号存储
            File file = new File(chunkPath + i);
            boolean newFile = file.createNewFile();
            if (newFile) {
                // 向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                    // 写入内容后文件大小上升，当块大小大于1M开始写下一块，
                    if (file.length() > chunkSize) {
                        break;
                    }
                }
                raf_write.close(); // 此块写完毕，写下一块
            }

        }
        raf_read.close(); // 所有块读完，关闭随机读写文件对象
    }

    @Test
    public void testFileMerge() throws IOException {
        // 块文件目录
        String chunkPath = "D:/develop/ffmpeg/chunk/";
        File chunkFolder = new File(chunkPath);
        // 合并文件
        File mergeFile = new File("D:/develop/video/merge.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        // 创建新的合并文件
        boolean newFile = mergeFile.createNewFile();
        if (newFile) {
            // 用于写文件
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
            // 指针指向文件顶端
            raf_write.seek(0);
            // 缓冲区
            byte[] bytes = new byte[1024];
            // 分块列表
            File[] fileArray = chunkFolder.listFiles();
            // 转成集合便于排序
            ArrayList<File> fileList = new ArrayList<>(Arrays.asList(fileArray));
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (Integer.parseInt(o1.getName())<Integer.parseInt(o2.getName())) {
                        //o1-o2升序排列
                        return -1;// 升序排列
                    }
                    return 1;
                }
            });
            // 合并文件
            for (File chunkFile : fileList) {
                RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "r");
                int len=-1;
                while ((len=raf_read.read(bytes))!=-1) {
                    raf_write.write(bytes,0,len);
                }
                raf_read.close();
            }
            raf_write.close();
        }

    }
}
