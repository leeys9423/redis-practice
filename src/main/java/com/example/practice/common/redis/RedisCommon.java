package com.example.practice.common.redis;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisCommon {

    private final RedisTemplate<String, String> template;
    private final Gson gson;

    @Value("${spring.cache.redis.time-to-live}")
    private Duration defaultExpireTime;

    public <T> T getData(String key, Class<T> clazz) {
        String jsonValue = template.opsForValue().get(key);
        if (jsonValue == null) {
            return null;
        }

        return gson.fromJson(jsonValue, clazz);
    }

    public <T> void setData(String key, T value) {
        String jsonValue = gson.toJson(value);
        template.opsForValue().set(key, jsonValue);
        template.expire(key, defaultExpireTime);
    }

    public <T> void multiSetData(Map<String, T> datas) {
        Map<String, String> jsonMap = new HashMap<>();
        for (Map.Entry<String, T> entry : datas.entrySet()) {
            jsonMap.put(entry.getKey(), gson.toJson(entry.getValue()));
        }

        template.opsForValue().multiSet(jsonMap);
    }
}
