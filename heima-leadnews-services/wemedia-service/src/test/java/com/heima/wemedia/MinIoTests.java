package com.heima.wemedia;

import com.heima.file.service.FileStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @Author : MR.wu
 * @Description : minio测试类
 * @Date : 2022/12/29 15:13
 * @Version : 1.0
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class MinIoTests {

    @Resource(name = "minIOFileStorageService")
    private FileStorageService fileStorageService;
    @Autowired
    private FileStorageService fileStorageService2;

    @Test
    public void uploadToMinIo() throws FileNotFoundException {
        System.out.println(fileStorageService);
        System.out.println(fileStorageService2);
        // 准备好一个静态页
        FileInputStream fileInputStream = new FileInputStream("D://text.html");
        // 将静态页上传到minIO文件服务器中          文件名称            文件类型             文件流
        fileStorageService.store("aa", "list.html", "text/html", fileInputStream);
    }
}
