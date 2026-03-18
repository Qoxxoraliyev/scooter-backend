package com.scooter_backend.config;

import com.scooter_backend.constant.RedisChannels;
import com.scooter_backend.service.redis.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisListenerConfig {

    private final RedisConnectionFactory connectionFactory;
    private final RedisSubscriber redisSubscriber;

    public RedisListenerConfig(RedisConnectionFactory connectionFactory, RedisSubscriber redisSubscriber) {
        this.connectionFactory = connectionFactory;
        this.redisSubscriber = redisSubscriber;
    }

    @Bean
    public RedisMessageListenerContainer container() {
        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(
                redisSubscriber,
                new PatternTopic(RedisChannels.SCOOTER_LOCATION)
        );

        return container;
    }
}
