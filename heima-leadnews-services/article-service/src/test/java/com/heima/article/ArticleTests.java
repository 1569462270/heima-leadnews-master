package com.heima.article;

import com.heima.article.service.ApArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author : MR.wu
 * @Description : 文章测试类
 * @Date : 2022/12/28 17:55
 * @Version : 1.0
 */
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleTests {
    @Autowired
    private ApArticleService articleService;

    @Test
    public void publishArticleTest() {
        articleService.publishArticle(6271);
    }
}
