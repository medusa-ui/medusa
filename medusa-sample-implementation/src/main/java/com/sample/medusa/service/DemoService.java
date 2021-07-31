package com.sample.medusa.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DemoService {

    public String uuid() {
        return UUID.randomUUID().toString();
    }
}
