package com.wang.gmall.manage;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GmallManageWebApplicationTests {

    @Test
    void contextLoads() throws IOException, MyException {
        // 配置fdfs的全局链接地址
        String tracker = GmallManageWebApplicationTests.class.getResource("/tracker.conf").getPath();// 获得配置文件的路径

        ClientGlobal.init(tracker);

        TrackerClient trackerClient = new TrackerClient();

        // 获得一个trackerServer的实例
        TrackerServer trackerServer = trackerClient.getConnection();

        // 通过tracker获得一个Storage链接客户端
        StorageClient storageClient = new StorageClient(trackerServer,null);

        String[] uploadInfos = storageClient.upload_file("d:/20.jpg", "jpg", null);

        String url = "http://192.168.83.128";

        for (String uploadInfo : uploadInfos) {
            url += "/"+uploadInfo;

            //url = url + uploadInfo;
        }

        System.out.println(url);
    }

}
