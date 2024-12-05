package com.koi.api.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfiguration {

    @Bean("emailQueue")
    public Queue emailQueue() {
        return QueueBuilder
                .durable("mail").build();
    }

    /**
     * This need to fix the "Failed to convert message" error, need to convert the queue message
     * @return
     */
    @Bean
    Jackson2JsonMessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
