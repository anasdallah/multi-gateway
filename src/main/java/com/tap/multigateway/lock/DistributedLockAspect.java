package com.tap.multigateway.lock;

import com.tap.multigateway.constant.ApiErrors;
import com.tap.multigateway.exception.TapException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;


@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

    private final RedisLock redisLock;

    @Around("@annotation(lock)")
    public Object distributedLock(ProceedingJoinPoint joinPoint, DistributedLock lock) throws Throwable {

        String lockKeyFieldName = lock.lockKey();
        String lockKey = null;

        if (!lockKeyFieldName.isEmpty()) {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                Object firstArg = args[0];
                Field field = firstArg.getClass().getDeclaredField(lockKeyFieldName);
                field.setAccessible(true);
                lockKey = (String) field.get(firstArg); // the field should be String
            }
        }

        boolean lockAcquired = redisLock.lock(lockKey, lock.timeout());

        if (!lockAcquired) {
            throw TapException.badRequest(ApiErrors.REQUEST_ALREADY_PROCESSED_BEFORE, lockKey);
        }

        try {
            log.info("request with id: [" + lockKey + "] is locked for " + lock.timeout() + " seconds.");

            // Execute the method
            return joinPoint.proceed();

        } finally {
            log.info("request with id: [" + lockKey + "] is unlocked");

            redisLock.unlock(lock.lockKey());
        }
    }
}