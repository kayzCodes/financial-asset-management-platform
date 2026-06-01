package uk.ac.rhul.cs3821.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the application's caching layer.
 * Sets up a Caffeine-based CacheManager to handle data expiration and eviction.
 */
@Configuration
public class CacheConfig {

  /**
   * Creates the CacheManager bean with a fixed configuration.
   * Configures a 12-hour expiration policy and a maximum size of 1000 entries
   * for the 'stockOverviewLong' and 'stockDailySeries' caches.
   *
   * @return the configured CaffeineCacheManager.
   */
  @Bean
  public CacheManager cacheManager() {

    CaffeineCacheManager manager = new CaffeineCacheManager();

    manager.registerCustomCache(
            "stockOverviewLong",
            Caffeine.newBuilder()
                    .expireAfterWrite(12, TimeUnit.HOURS)
                    .maximumSize(1000)
                    .build()
    );

    manager.registerCustomCache(
            "stockDailySeries",
            Caffeine.newBuilder()
                    .expireAfterWrite(12, TimeUnit.HOURS)
                    .maximumSize(1000)
                    .build()
    );

    manager.registerCustomCache(
            "portfolioOverview",
            Caffeine.newBuilder()
                    .expireAfterWrite(60, TimeUnit.SECONDS)
                    .maximumSize(1000)
                    .build()
    );

    return manager;
  }

}
