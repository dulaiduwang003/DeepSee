package com.cn.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * redis lock
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public final class RedisLockHelper {


    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Lock boolean.
     *
     * @param targetKey the target key
     * @return the boolean
     */
    public boolean lock(String targetKey, long lockTimeout) {
        String timeStamp = String.valueOf(System.currentTimeMillis() + lockTimeout);
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(targetKey, timeStamp, lockTimeout, TimeUnit.MILLISECONDS);
        if (lockAcquired != null && lockAcquired) {
            return true;
        }

        String currentLock = redisTemplate.opsForValue().get(targetKey);
        if (StringUtils.isNotEmpty(currentLock)) {
            long currentLockExpireTime = Long.parseLong(currentLock);
            if (currentLockExpireTime < System.currentTimeMillis()) {
                String previousLock = redisTemplate.opsForValue().getAndSet(targetKey, timeStamp);
                return StringUtils.isNotEmpty(previousLock) && previousLock.equals(currentLock);
            }
        }
        return false;
    }

    public void unlock(String targetKey) {
        try {
            String currentValue = redisTemplate.opsForValue().get(targetKey);
            if (StringUtils.isNotEmpty(currentValue)) {
                redisTemplate.delete(targetKey);
            }
        } catch (Exception e) {
            log.error("Failed to unlock key: {}, error message: {}", targetKey, e.getMessage());
        }
    }

}
