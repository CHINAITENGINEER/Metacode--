package com.huakang.service.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis配置
 *
 * @author huakang
 */
@Configuration
@EnableCaching  // 启用Spring Cache
public class RedisConfig {

    /**
     * 创建ObjectMapper，支持Java 8时间类型
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 注册Java 8时间模块，支持LocalDateTime等类型
        mapper.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * RedisTemplate配置
     */
    @Bean
    @SuppressWarnings({"deprecation", "removal"})
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = createObjectMapper();
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        // 注意：setObjectMapper 已过时，但在新版本 API 可用前仍需要使用
        serializer.setObjectMapper(mapper);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 缓存管理器配置
     * 为不同的缓存区域设置不同的过期时间
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 创建支持Java 8时间类型的序列化器
        ObjectMapper cacheMapper = createObjectMapper();
        GenericJackson2JsonRedisSerializer cacheSerializer = new GenericJackson2JsonRedisSerializer(cacheMapper);

        // 默认缓存配置：5分钟过期
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(cacheSerializer))
                .disableCachingNullValues(); // 不缓存null值，防止缓存穿透

        // 为不同缓存区域设置不同的过期时间
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 数据大屏缓存：1分钟过期（数据变化频繁）
        cacheConfigurations.put("dashboard_stats",
                defaultConfig.entryTtl(Duration.ofMinutes(1)));
        cacheConfigurations.put("dashboard_trend",
                defaultConfig.entryTtl(Duration.ofMinutes(1)));

        // 商品缓存：5分钟过期
        cacheConfigurations.put("products",
                defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // 会员缓存：10分钟过期
        cacheConfigurations.put("members",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // 系统配置缓存：30分钟过期（变化频率低）
        cacheConfigurations.put("system_configs",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 管理员/店员缓存：10分钟过期
        cacheConfigurations.put("admins",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("staffs",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware() // 支持事务
                .build();
    }
}
