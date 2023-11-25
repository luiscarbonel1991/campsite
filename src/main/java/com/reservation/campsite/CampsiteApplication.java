package com.reservation.campsite;

import com.reservation.campsite.configuration.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

@SpringBootApplication
@Slf4j
public class CampsiteApplication {

    private final CacheManager cacheManager;

    public CampsiteApplication(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(CampsiteApplication.class, args);
    }


    @Bean
    CommandLineRunner runner() {
        return args -> {
            log.info("Clearing cache");
            Objects.requireNonNull(cacheManager.getCache(CacheConfig.AVAILABILITY_RANGE_DATES_CACHE)).clear();
        };
    }
}
