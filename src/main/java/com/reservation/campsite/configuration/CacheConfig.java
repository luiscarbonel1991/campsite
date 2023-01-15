package com.reservation.campsite.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {


    public static final String AVAILABILITY_RANGE_DATES_CACHE = "availabilityRangeDates";

    @Bean
    public RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()
                ));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
            @Value("${cache.availabilityRangeDates.ttlMinutes:1}") int availabilityRangeDatesTtlMinutes
    ) {
        return builder -> builder.withCacheConfiguration(AVAILABILITY_RANGE_DATES_CACHE,
                defaultCacheConfiguration().entryTtl(Duration.ofMinutes(availabilityRangeDatesTtlMinutes)));
    }
}
