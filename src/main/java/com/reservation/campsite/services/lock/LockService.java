package com.reservation.campsite.services.lock;

import java.util.function.Supplier;

public interface LockService {

    <T> T lock(String key, Supplier<T> supplier, Integer timeout);
}
