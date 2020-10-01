package com.xuecheng.manage_cms.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    // 交换机名称
    public static final String EX_ROUTING_CMS_POSTPAGE = "ex_routing_cms_postpage";

    //声明交换机,使用direct类型
    @Bean(EX_ROUTING_CMS_POSTPAGE)
    public Exchange EXCHANGE_TOPICS_INFORM() {
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.directExchange(EX_ROUTING_CMS_POSTPAGE).durable(true).build();
    }

}
