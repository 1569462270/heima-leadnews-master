package com.heima.wemedia;

import com.heima.aliyun.GreenImageScan;
import com.heima.aliyun.GreenTextScan;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author : MR.wu
 * @Description : 阿里云内容安全测试类
 * @Date : 2022/12/25 20:22
 * @Version : 1.0
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class AliyunSecurityTests {

    @Autowired
    private GreenTextScan greenTextScan;
    @Autowired
    private GreenImageScan greenImageScan;

    @Test
    public void testText() throws Exception {
        Map map = greenTextScan.greenTextScan("我是一个文本,冰毒买卖是违法的");
        System.out.println(map);
    }

    @Test
    public void testImage() throws Exception {
        List<String> images = new ArrayList<>();
        images.add("https://hhtt-ag.oss-cn-hangzhou.aliyuncs.com/material/2022/12/20221224/6d1907797b59436bb1ac19cfbcbf6d0f.jpg");
        Map map = greenImageScan.imageUrlScan(images);
        System.out.println(map);
    }
}
