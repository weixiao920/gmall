package com.wang.gmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author 微笑
 * @date 2019/11/15 17:39
 */
public class PmsUploadUtil {
    public static String uploadImage(MultipartFile multipartFile) {
        String url = "http://192.168.83.128";
        /**
         * 配置fdfs的全局链接地址
         * 获得配置文件的路径
          */
        String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();
        try {
            ClientGlobal.init(tracker);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TrackerClient trackerClient = new TrackerClient();

        // 获得一个trackerServer的实例
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 通过tracker获得一个Storage链接客户端
        StorageClient storageClient = new StorageClient(trackerServer,null);

        try {
            /**
             * 获得上传的二进制对象
             */
            byte[] bytes = multipartFile.getBytes();
            /**
             * 1获取文件全名 a.jpg
             * 2获取文件后缀名
             *   .1 获取最后一个点的坐标
             *   .2根据坐标得到后缀名
             */
            String originalFilename = multipartFile.getOriginalFilename();
            int lastIndexOf= originalFilename.lastIndexOf(".");
            String file_ext_name = originalFilename.substring(lastIndexOf + 1);

            String[]   uploadInfos = storageClient.upload_file(bytes, file_ext_name, null);
            for (String uploadInfo : uploadInfos) {
                url += "/"+uploadInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return url;
    }
}
