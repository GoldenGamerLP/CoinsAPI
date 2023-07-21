package me.alex.coinsapi.implementation.event;

import dev.hypera.chameleon.event.EventSubscriber;
import dev.hypera.chameleon.event.EventSubscriptionPriority;
import dev.hypera.chameleon.event.common.UserDisconnectEvent;
import me.alex.coinsapi.api.CoinsUserCache;
import me.alex.coinsapi.implementation.CoinsAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PlayerLeaveEvent implements EventSubscriber<UserDisconnectEvent> {

    private final CoinsUserCache userCache;
    private final Executor executor;

    public PlayerLeaveEvent(CoinsAPI plugin) {
        this.userCache = plugin.getCache();
        this.executor = plugin.getExecutor().getExecutor();
        plugin.getChameleon().getEventBus().subscribe(this);
    }

    @Override
    public void on(@NotNull UserDisconnectEvent event) {
        CompletableFuture.runAsync(() -> userCache.invalidate(event.getUser().getId()), executor);
    }

    @Override
    public @NotNull EventSubscriptionPriority getPriority() {
        return EventSubscriptionPriority.LAST;
    }

    @Override
    public boolean acceptsCancelled() {
        return true;
    }

    @Override
    public @Nullable Class<UserDisconnectEvent> getType() {
        return UserDisconnectEvent.class;
    }
}
