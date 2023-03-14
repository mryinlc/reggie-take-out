package com.reggie.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reggie.common.FastJsonRedisSerializer;
import com.reggie.common.JacksonObjectMapper;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration(proxyBeanMethods = false)
public class RedisConfig extends CachingConfigurerSupport {
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        // 设置key序列化器为string类型
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(new JacksonObjectMapper());
        // // GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(new ObjectMapper());
        // // 不设置ObjectMapper的话，序列化的JSON串中包含@class信息，使用自行传入的ObjectMapper后则不包含
        // // 底层原因是public GenericJackson2JsonRedisSerializer(@Nullable String classPropertyTypeName)函数中对mapper进行了改造
        // redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        // redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        // 这是FastJSON可以autoType的包名白名单，不设置的话在反序列化时会报错
        ParserConfig.getGlobalInstance().addAccept("com.reggie.pojo.");

        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}
