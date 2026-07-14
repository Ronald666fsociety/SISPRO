package com.transandina.sigepro.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_SECONDS = 300;

    public boolean isBlocked(String ip) {
        Attempt attempt = attempts.get(ip);
        if (attempt == null) return false;
        if (attempt.count >= MAX_ATTEMPTS) {
            if (Instant.now().getEpochSecond() - attempt.blockedAt < BLOCK_DURATION_SECONDS) {
                return true;
            }
            attempts.remove(ip);
        }
        return false;
    }

    public void registerFailed(String ip) {
        attempts.compute(ip, (key, val) -> {
            if (val == null) return new Attempt(1, Instant.now().getEpochSecond());
            Attempt updated = new Attempt(val.count + 1, val.blockedAt);
            if (updated.count >= MAX_ATTEMPTS) {
                updated.blockedAt = Instant.now().getEpochSecond();
            }
            return updated;
        });
    }

    public void reset(String ip) {
        attempts.remove(ip);
    }

    public int getRemainingAttempts(String ip) {
        Attempt attempt = attempts.get(ip);
        if (attempt == null) return MAX_ATTEMPTS;
        return Math.max(0, MAX_ATTEMPTS - attempt.count);
    }

    private static class Attempt {
        int count;
        long blockedAt;
        Attempt(int count, long blockedAt) {
            this.count = count;
            this.blockedAt = blockedAt;
        }
    }
}
