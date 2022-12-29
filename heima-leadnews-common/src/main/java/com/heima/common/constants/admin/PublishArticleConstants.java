package com.heima.common.constants.admin;

/**
 * @Author : MR.wu
 * @Description : 发布文章延迟队列常量
 * @Date : 2022/12/28 20:26
 * @Version : 1.0
 */
public class PublishArticleConstants {
    /**
     * 发布文章队列
     */
    public static final String PUBLISH_ARTICLE_QUEUE = "publish.article.queue";

    /**
     * 通往发布文章队列的路由key
     */
    public static final String PUBLISH_ARTICLE_ROUTE_KEY = "delay.publish.article";

    /**
     * 延时队列交换机
     */
    public static final String DELAY_DIRECT_EXCHANGE = "delay.direct";
}
