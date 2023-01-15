package com.reservation.campsite.services.lock;

import com.reservation.campsite.exception.BadRequestException;
import com.reservation.campsite.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

@Component
@Slf4j
public class LockServiceImpl implements LockService {
    private final LockRegistry lockRegistry;


    public LockServiceImpl(LockRegistry lockRegistry) {
        this.lockRegistry = lockRegistry;
    }

    @Override
    public <T> T lock(String key, Supplier<T> supplier, Integer timeout) {
        int toTime = timeout != null ? timeout : 10;
        Lock lock = lockRegistry.obtain(key);
        try {
            if (lock.tryLock(toTime, TimeUnit.SECONDS)) {
                return execute(supplier);
            } else {
                log.error("Lock not acquired for key: {}", key);
                throw BadRequestException.lock();
            }
        } catch (BusinessException e) {
            throw e;
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            throw BadRequestException.lock();
        } finally {
            lock.unlock();
        }
    }

    private static <T> T execute(Supplier<T> supplier) {
        return supplier.get();
    }



   /*
   * Possible retry implementation

    @Override
    public <T> T lock(String key, Supplier<T> supplier, Integer timeout) {
        int toTime = timeout != null ? timeout : 10;
        Lock lock = lockRegistry.obtain(key);
        try {
            return tryToGetLock( () -> {
                try {
                    if(Boolean.FALSE.equals(lock.tryLock(toTime, TimeUnit.SECONDS))) {
                        return null;
                    }

                    log.info("Successfully acquired lock for key '{}'", key);
                    return supplier.get();
                } catch (Exception e) {
                   log.error("Error trying to get lock", e);
                   return null;
                } finally {
                    lock.unlock();
                }
            },key, toTime);

        } catch (Exception e) {
            log.error("Error locking: {}", e.getMessage());
           // redisTemplate.delete("campsite_lock:" +  key);
            return null;
        }
    }
    *
    private static <T> T tryToGetLock(final Supplier<T> task,
                                      final String lockKey,
                                      final int howLongShouldLockBeAcquiredSeconds) throws Exception {
        final long tryToGetLockTimeout = TimeUnit.SECONDS.toMillis(howLongShouldLockBeAcquiredSeconds);

        final long startTimestamp = Instant.now().toEpochMilli();
        while (true) {
            log.info("Trying to get the lock with key '{}'", lockKey);
            final T response = task.get();
            if (response != null) {
                return response;
            }
            sleep(DEFAULT_RETRY_TIME);

            if (Instant.now().toEpochMilli() - startTimestamp > tryToGetLockTimeout) {
                throw new Exception("Failed to acquire lock in " + tryToGetLockTimeout + " milliseconds");
            }
        }
    }
    */

}
