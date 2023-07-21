package me.alex.coinsapi.implementation.threads;

import me.alex.coinsapi.implementation.CoinsAPI;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CoinThreadFactory implements ThreadFactory {

    private static final String NAME = "CoinsAPI-Thread-";
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final Logger logger;

    CoinThreadFactory(CoinsAPI plugin) {
        this.logger = plugin.getLogger();
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r, NAME + threadNumber.getAndIncrement());
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> logger.error("An error occurred in thread {} with error: {}", t.getName(), e.getLocalizedMessage()));
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.setContextClassLoader(getClass().getClassLoader());
        return thread;
    }
}
