package com.heima.wemedia;

import com.heima.wemedia.service.WmNewsAutoScanService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author : MR.wu
 * @Description : 文章自动审核测试类
 * @Date : 2022/12/26 20:07
 * @Version : 1.0
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class WmNewsAutoScanTests {
    @Autowired
    private WmNewsAutoScanService autoScanService;

    @Test
    public void handleTextAndImages() {
        autoScanService.autoScanWmNews(6271);
    }
}
