package ru.mai.springaop;

import org.springframework.stereotype.Service;
import ru.mai.springaop.cache.anntations.CacheDelete;
import ru.mai.springaop.cache.anntations.CacheGet;
import ru.mai.springaop.cache.anntations.CacheUpdate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CacheService {

    private final Map<Integer, String> database = new HashMap<>(); // Имитация базы данных

    @CacheUpdate
    public void put(String object) {
        database.put(object.hashCode(), object);
    }

    @CacheGet
    public Object get(String object) {
        return database.get(object.hashCode());
    }

    @CacheDelete
    public void remove(String object) {
        database.remove(object.hashCode());
    }

}
