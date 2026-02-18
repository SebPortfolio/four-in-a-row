package de.paulm.four_in_a_row.web.security;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bucket;

@Service
public class RateLimitService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> Bucket.builder()
                .addLimit(limit -> limit.capacity(50).refillGreedy(50, Duration.ofMinutes(1)))
                .addLimit(limit -> limit.capacity(15).refillGreedy(15, Duration.ofSeconds(10)))
                .build());
    }

}
