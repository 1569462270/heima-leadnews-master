package com.heima.wemedia;

import com.heima.file.service.FileStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;

/**
 * @Author : MR.wu
 * @Description : oss测试类
 * @Date : 2022/12/24 13:41
 * @Version : 1.0
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class OssTests {

    @Autowired
    private FileStorageService fileStorageService;


    @Value("${file.oss.web-site}")
    String webSite;

    @Test
    public void fileUploadTest() {
        try (FileInputStream fis = new FileInputStream(new File("D:/myfile/picture/1.jpg"));) {
            String url = fileStorageService.store("test", "1.jpg", fis);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
