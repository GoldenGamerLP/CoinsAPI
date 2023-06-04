package me.alex.coinsapi.implementation.event;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.event.EventSubscriber;
import dev.hypera.chameleon.event.EventSubscriptionPriority;
import dev.hypera.chameleon.event.common.UserDisconnectEvent;
import me.alex.coinsapi.implementation.CoinsAPI;
import me.alex.coinsapi.implementation.data.UserCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerLeaveEvent implements EventSubscriber<UserDisconnectEvent> {

    private final Chameleon chameleon;
    private final UserCache userCache;

    public PlayerLeaveEvent(CoinsAPI plugin) {
        this.chameleon = plugin.getChameleon();
        this.userCache = plugin.getUserCache();

        plugin.getChameleon().getEventBus().subscribe(this);
    }

    @Override
    public void on(@NotNull UserDisconnectEvent event) throws Exception {
        userCache.getCache().invalidate(event.getUser().getId());
    }

    @Override
    public @NotNull EventSubscriptionPriority getPriority() {
        return EventSubscriptionPriority.NORMAL;
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
