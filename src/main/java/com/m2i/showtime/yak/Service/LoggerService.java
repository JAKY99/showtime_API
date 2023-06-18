package com.m2i.showtime.yak.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class LoggerService {
    private static final Logger LOGGER = LogManager.getLogger(LoggerService.class);
    public void print(String message) {
        LOGGER.trace(message);
        System.out.println(message);
    }
    public boolean printTest(String message) {
        LOGGER.trace(message);
        System.out.println(message);
        return true;
    }
}
