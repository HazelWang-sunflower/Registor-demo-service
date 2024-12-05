package com.koi.api.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

// using redis to limit the multiple request register code
@Component
public class FlowUtils {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    /**
     *
     * @param key check key
     * @param blockTime   请求的冷却时间
     * @return
     */
    public boolean limitOnceCheck(String key, int blockTime) {
        Boolean isHave = stringRedisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(isHave)){
            return false;
        } else {
            // 进入冷却时间等待
            stringRedisTemplate.opsForValue().set(key, "", blockTime, TimeUnit.SECONDS);
            return true;
        }
    }
}
