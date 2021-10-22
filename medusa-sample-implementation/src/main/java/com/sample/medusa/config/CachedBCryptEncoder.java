package com.sample.medusa.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.concurrent.TimeUnit;

public class CachedBCryptEncoder extends BCryptPasswordEncoder {

    static Cache<String, Boolean> cache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(250)
            .build();

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return Boolean.TRUE.equals(cache.get(rawPassword + "$" + encodedPassword, k -> super.matches(rawPassword, encodedPassword)));
    }
}
