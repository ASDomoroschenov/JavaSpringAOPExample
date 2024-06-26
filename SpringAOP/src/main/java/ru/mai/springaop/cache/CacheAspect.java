package ru.mai.springaop.cache;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.mai.springaop.cache.anntations.CacheDelete;
import ru.mai.springaop.cache.anntations.CacheGet;
import ru.mai.springaop.cache.anntations.CacheUpdate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class CacheAspect {

    private static final Map<String, Map<Integer, Object>> cacheManager = new HashMap<>();

    @Pointcut("@annotation(cacheUpdate) && args(putObject)")
    public void cacheUpdatePointCut(CacheUpdate cacheUpdate, Object putObject) {
    }

    @Pointcut("@annotation(cacheDelete) && args(removeObject)")
    public void cacheDeletePointCut(CacheDelete cacheDelete, Object removeObject) {
    }

    @Pointcut("@annotation(cacheGet) && args(getObject)")
    public void cacheGetPointCut(CacheGet cacheGet, Object getObject) {
    }

    @After(value = "cacheUpdatePointCut(cacheUpdate, putObject)", argNames = "joinPoint,cacheUpdate,putObject")
    public void afterCacheUpdate(JoinPoint joinPoint, CacheUpdate cacheUpdate, Object putObject) {
        Map<Integer, Object> cache = cacheManager.getOrDefault(cacheUpdate.name(), new HashMap<>());
        cache.put(putObject.hashCode(), putObject);
        cacheManager.putIfAbsent(cacheUpdate.name(), cache);

        log.info("Add object to cache");
    }

    @After(value = "cacheDeletePointCut(cacheDelete, removeObject)", argNames = "joinPoint,cacheDelete,removeObject")
    public void afterCacheUpdate(JoinPoint joinPoint, CacheDelete cacheDelete, Object removeObject) {
        Map<Integer, Object> cache = cacheManager.getOrDefault(cacheDelete.name(), new HashMap<>());

        if (cacheManager.containsKey(cacheDelete.name())) {
            cache.remove(removeObject.hashCode());
        }

        cacheManager.putIfAbsent(cacheDelete.name(), cache);

        log.info("Delete object from cache");
    }

    @Around(value = "cacheGetPointCut(cacheGet, getObject)", argNames = "pjp,cacheGet,getObject")
    public Object aroundCacheGet(ProceedingJoinPoint pjp, CacheGet cacheGet, Object getObject) throws Throwable {
        Map<Integer, Object> cache = cacheManager.getOrDefault(cacheGet.name(), new HashMap<>());
        int key = getObject.hashCode();

        if (!cache.containsKey(key)) {
            Object result = pjp.proceed();

            if (result != null) {
                cache.put(key, result);
            }

            cacheManager.putIfAbsent(cacheGet.name(), cache);

            log.info("Get object from database");

            return result;
        }

        log.info("Get object from cache");

        return cache.get(key);
    }

}
