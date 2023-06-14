package org.pot.core.data.cache;

import java.lang.reflect.ParameterizedType;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.WriteBehindConfigurationBuilder;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.loaderwriter.WriteBehindConfiguration;
import org.pot.core.constant.GlobalProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

public abstract class AbstractCache<K, V> implements CacheLoaderWriter<K, V> {

    private Cache<K, V> cache;

    @Autowired
    private CacheManager cacheManager;
    @Resource
    GlobalProperties properties;
    @Autowired
    public CrudRepository<V, K> repository;

    public AbstractCache(CrudRepository<V, K> repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        Class keyClass = (Class) parameterizedType.getActualTypeArguments()[0];
        Class valueClass = (Class) parameterizedType.getActualTypeArguments()[1];
        CacheConfigurationBuilder<K, V> builder = CacheConfigurationBuilder
            .newCacheConfigurationBuilder(keyClass, valueClass,
                ResourcePoolsBuilder.heap(properties.getCache().getMaxSize()));
        builder = builder.withLoaderWriter(this);
        WriteBehindConfiguration writeBehindConfiguration = getWriteBehindConfiguration();
        if (writeBehindConfiguration != null) {
            builder = builder.withService(writeBehindConfiguration);
        }
        builder = customizeCacheConfiguration(builder);
        CacheConfiguration<K, V> cacheConfiguration = builder.build();
        cache = cacheManager.createCache(getClass().getSimpleName(), cacheConfiguration);
    }

    protected WriteBehindConfiguration getWriteBehindConfiguration() {
        return WriteBehindConfigurationBuilder
            .newBatchedWriteBehindConfiguration(1, TimeUnit.SECONDS,
                properties.getCache().getBatch())
            .queueSize(10000000)
            .concurrencyLevel(properties.getCache().getConcurrency())
            .enableCoalescing()
            .useThreadPool("writeBehindPool")
            .build();
    }

    protected CacheConfigurationBuilder<K, V> customizeCacheConfiguration(
        CacheConfigurationBuilder<K, V> builder) {
        return builder;
    }

    public void save(K key, V value) {
        cache.put(key, value);
    }

    public V get(K key) {
        return cache.get(key);
    }

    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public V load(K key) throws Exception {
        return repository.findById(key).orElse(defaultInstance(key));
    }

    protected abstract V defaultInstance(K key);

    @Override
    public void write(K key, V value) throws Exception {
        repository.save(value);
    }

    @Override
    public void delete(K key) {
        repository.deleteById(key);
    }

}
