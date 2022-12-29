package com.heima.common.constants.admin;

/**
 * @Author : MR.wu
 * @Description : 文章上下架mq常量
 * @Date : 2022/12/29 12:47
 * @Version : 1.0
 */
public class NewsUpOrDownConstants {

    public static final String NEWS_UP_OR_DOWN_EXCHANGE = "wm.news.up.or.down.topic";

    public static final String NEWS_UP_FOR_ARTICLE_CONFIG_QUEUE = "news.up.for.article.config.queue";
    public static final String NEWS_UP_FOR_ES_QUEUE = "news.up.for.es.queue";

    public static final String NEWS_DOWN_FOR_ES_QUEUE = "news.down.for.es.queue";
    public static final String NEWS_DOWN_FOR_ARTICLE_CONFIG_QUEUE = "news.down.for.article.config.queue";

    public static final String NEWS_UP_ROUTE_KEY = "news.up";
    public static final String NEWS_DOWN_ROUTE_KEY = "news.down";
}
