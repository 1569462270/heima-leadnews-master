package com.heima.wemedia.config;

import com.heima.common.constants.admin.PublishArticleConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : MR.wu
 * @Description : mq延迟队列配置类
 * @Date : 2022/12/28 20:29
 * @Version : 1.0
 */
@Configuration
public class DeclarePubArticleRabbitConfig {

    /**
     * 延时交换机
     */
    @Bean
    public DirectExchange delayExchange() {
        return ExchangeBuilder.directExchange(PublishArticleConstants.DELAY_DIRECT_EXCHANGE)
                .delayed()
                .durable(true)
                .build();
    }

    /**
     * 声明发布文章队列
     */
    @Bean
    public Queue publishArticleQueue() {
        return new Queue(PublishArticleConstants.PUBLISH_ARTICLE_QUEUE, true);
    }

    /**
     * 绑定 延迟交换机 + 发布文章队列
     */

    @Bean
    public Binding bindingDeadQueue() {
        return BindingBuilder.bind(publishArticleQueue()).to(delayExchange()).with(PublishArticleConstants.PUBLISH_ARTICLE_ROUTE_KEY);
    }
}
