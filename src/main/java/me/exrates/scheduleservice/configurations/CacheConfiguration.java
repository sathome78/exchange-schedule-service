package me.exrates.scheduleservice.configurations;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@EnableCaching
public class CacheConfiguration {

    public static final String CURRENCY_CACHE = "currency-cache";
    public static final String ALL_RATES_CACHE = "all-rates-cache";
    public static final String ALL_BALANCES_CACHE = "all-balances-cache";

    @Bean(CURRENCY_CACHE)
    public Cache cacheCurrency() {
        return new CaffeineCache(CURRENCY_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }

    @Bean(ALL_RATES_CACHE)
    public Cache cacheAllRates() {
        return new CaffeineCache(ALL_RATES_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }

    @Bean(ALL_BALANCES_CACHE)
    public Cache cacheAllBalances() {
        return new CaffeineCache(ALL_BALANCES_CACHE, Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build());
    }
}
