package me.alex.coinsapi.implementation.event;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.event.EventSubscriber;
import dev.hypera.chameleon.event.EventSubscriptionPriority;
import dev.hypera.chameleon.event.common.UserDisconnectEvent;
import me.alex.coinsapi.api.CoinsUserCache;
import me.alex.coinsapi.implementation.CoinsAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerLeaveEvent implements EventSubscriber<UserDisconnectEvent> {

    private final Chameleon chameleon;
    private final CoinsUserCache userCache;

    public PlayerLeaveEvent(CoinsAPI plugin) {
        this.chameleon = plugin.getChameleon();
        this.userCache = plugin.getCache();

        plugin.getChameleon().getEventBus().subscribe(this);
    }

    @Override
    public void on(@NotNull UserDisconnectEvent event) throws Exception {
        userCache.invalidate(event.getUser().getId());
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
