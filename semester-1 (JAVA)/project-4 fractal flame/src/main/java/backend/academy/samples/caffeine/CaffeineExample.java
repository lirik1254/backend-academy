package backend.academy.samples.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;

/**
 * Caffeine is a high-performance caching library with Map-like interface.
 * Do not confuse with {@link com.google.common.cache.Cache}!
 * <p>
 * <a href="https://github.com/ben-manes/caffeine/wiki">Library documentation</a>
 */
@Log4j2
public class CaffeineExample {
    public static void main(String[] args) throws InterruptedException {
        Cache<String, String> cache = Caffeine.newBuilder()
            .maximumSize(3)
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

        cache.put("key1", "value1");
        var firstVal = cache.getIfPresent("key1");
        cache.put("key2", "value2");
        var secondVal = cache.getIfPresent("key2");
        var thirdVal = cache.getIfPresent("key3");
        log.info("First value: {}, second value: {}, third value: {}", firstVal, secondVal, thirdVal);

        log.info("Current Cache state: {}", cache.asMap());
        Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));
        log.info("Cache state after expiration: {}", cache.asMap());

        LoadingCache<String, String> loadingCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .maximumSize(10)
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    log.info("Loading with key: {}", key);
                    return key + "value";
                }
            });

        loadingCache.put("key1", "value1");
        loadingCache.get("key2");
        loadingCache.get("key2");
        log.info("Current LoadingCache state: {}", loadingCache.asMap());
    }
}
