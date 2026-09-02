package com.vanvat.moviestream.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates simple sequential, human-readable ids like "MV-1", "CT-1".
 * A real DB would use auto-increment; here the repository tells us the
 * current max so ids stay stable across restarts.
 */
public final class IdGenerator {

    private final String prefix;
    private final AtomicInteger counter;

    public IdGenerator(String prefix, int startAfter) {
        this.prefix = prefix;
        this.counter = new AtomicInteger(startAfter);
    }

    public String next() {
        return prefix + "-" + counter.incrementAndGet();
    }
}
