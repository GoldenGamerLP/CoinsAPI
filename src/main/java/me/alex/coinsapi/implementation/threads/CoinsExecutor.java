package me.alex.coinsapi.implementation.threads;

import lombok.Getter;
import me.alex.coinsapi.implementation.CoinsAPI;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class CoinsExecutor {

    @Getter
    private Executor executor;

    public CoinsExecutor(CoinsAPI plugin) {
        int threads = plugin.getConfig().getThreads_threshold();
        ThreadFactory factory = new CoinThreadFactory(plugin);
        executor = Executors.newFixedThreadPool(threads, factory);
    }
}
