package backend.academy.scrapper.config;

import dto.UpdatePayload;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofSeconds(30))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer())))
                .build();
    }

    @Bean
    public RedisTemplate<Long, UpdatePayload> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Long, UpdatePayload> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new GenericToStringSerializer<>(Long.class));

        Jackson2JsonRedisSerializer<UpdatePayload> ser = new Jackson2JsonRedisSerializer<>(UpdatePayload.class);
        template.setValueSerializer(ser);
        template.afterPropertiesSet();
        return template;
    }
}
