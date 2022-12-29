package com.heima.wemedia.config;


import com.heima.common.constants.admin.NewsUpOrDownConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : MR.wu
 * @Description : 文章上下架
 * @Date : 2022/12/29 12:48
 * @Version : 1.0
 */
@Configuration
public class DeclareUpOrDownRabbitConfig {
    @Bean
    public TopicExchange newsUpOrDownTopicExchange() {
        return new TopicExchange(NewsUpOrDownConstants.NEWS_UP_OR_DOWN_EXCHANGE, true, false);
    }

    @Bean
    public Queue newsUpForArticleConfig() {
        return new Queue(NewsUpOrDownConstants.NEWS_UP_FOR_ARTICLE_CONFIG_QUEUE, true);
    }

    @Bean
    public Queue newsUpForES() {
        return new Queue(NewsUpOrDownConstants.NEWS_UP_FOR_ES_QUEUE, true);
    }

    @Bean
    public Queue newsDownForArticleConfig() {
        return new Queue(NewsUpOrDownConstants.NEWS_DOWN_FOR_ARTICLE_CONFIG_QUEUE, true);
    }

    @Bean
    public Queue newsDownForES() {
        return new Queue(NewsUpOrDownConstants.NEWS_DOWN_FOR_ES_QUEUE, true);
    }

    @Bean
    public Binding bindingNewsUpForES() {
        return BindingBuilder.bind(newsUpForES()).to(newsUpOrDownTopicExchange()).with(NewsUpOrDownConstants.NEWS_UP_ROUTE_KEY);
    }

    @Bean
    public Binding bindingNewsUpForArticleConfig() {
        return BindingBuilder.bind(newsUpForArticleConfig()).to(newsUpOrDownTopicExchange()).with(NewsUpOrDownConstants.NEWS_UP_ROUTE_KEY);
    }

    @Bean
    public Binding bindingNewsDownForES() {
        return BindingBuilder.bind(newsDownForES()).to(newsUpOrDownTopicExchange()).with(NewsUpOrDownConstants.NEWS_DOWN_ROUTE_KEY);
    }

    @Bean
    public Binding bindingNewsDownForArticleConfig() {
        return BindingBuilder.bind(newsDownForArticleConfig()).to(newsUpOrDownTopicExchange()).with(NewsUpOrDownConstants.NEWS_DOWN_ROUTE_KEY);
    }
}
