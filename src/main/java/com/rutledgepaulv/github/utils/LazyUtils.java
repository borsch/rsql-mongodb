package com.rutledgepaulv.github.utils;

import java.util.List;

import com.rutledgepaulv.github.structs.Lazy;

public final class LazyUtils {

    private LazyUtils() {}

    public static <T> T firstThatReturnsNonNull(List<Lazy<T>> lazies) {
        for(Lazy<T> lazy : lazies) {
            T val = lazy.get();
            if(val != null) {
                return val;
            }
        }
        return null;
    }

}
