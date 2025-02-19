package lissa.trading.user.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Value("${cache.redis.default-expiration}")
    private Duration defaultExpiration;

    @Value("${cache.redis.short-term-expiration}")
    private Duration shortTermExpiration;

    private final StringRedisSerializer stringRedisSerializer
            = new StringRedisSerializer();
    private final GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer =
            new GenericJackson2JsonRedisSerializer();

    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(defaultExpiration)
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                             .fromSerializer(jackson2JsonRedisSerializer));
    }

    @Bean
    public RedisCacheConfiguration shortTermCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(shortTermExpiration)
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                             .fromSerializer(jackson2JsonRedisSerializer));
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                               RedisCacheConfiguration defaultCacheConfiguration,
                                               RedisCacheConfiguration shortTermCacheConfiguration) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                "users", defaultCacheConfiguration,
                "stockPrices", shortTermCacheConfiguration
        );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .enableStatistics()
                .build();
    }
}
