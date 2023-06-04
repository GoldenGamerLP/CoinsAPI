package me.alex.coinsapi.implementation.event;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.event.EventSubscriber;
import dev.hypera.chameleon.event.EventSubscriptionPriority;
import dev.hypera.chameleon.event.common.UserConnectEvent;
import dev.hypera.chameleon.scheduler.Scheduler;
import dev.hypera.chameleon.scheduler.Task;
import dev.hypera.chameleon.user.User;
import me.alex.coinsapi.api.CoinUser;
import me.alex.coinsapi.api.CoinUserDAO;
import me.alex.coinsapi.implementation.CoinsAPI;
import me.alex.coinsapi.implementation.data.pojo.UserImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PlayerLoginEvent implements EventSubscriber<UserConnectEvent> {

    private final Chameleon chameleon;
    private final CoinUserDAO dao;
    private final Scheduler scheduler;

    public PlayerLoginEvent(CoinsAPI plugin) {
        this.chameleon = plugin.getChameleon();
        this.dao = plugin.getDatabase();
        this.scheduler = chameleon.getScheduler();

        plugin.getChameleon().getEventBus().subscribe(this);
    }

    @Override
    public void on(@NotNull UserConnectEvent event) throws Exception {
        User user = event.getUser();

        scheduler.schedule(Task.async(() -> {
            Optional<CoinUser> coinUser = dao.getUser(user.getId());

            //Create user if needed
            if (coinUser.isEmpty()) {
                CoinUser newUser = new UserImpl(user.getId(), 0L, user.getName(), 0D);
                dao.saveUser(newUser);
            }

            //Update name if needed
            coinUser.ifPresent(coinUser1 -> {
                UserImpl userImpl = (UserImpl) coinUser1;
                if (!userImpl.getLastKnownName().equals(user.getName())) return;

                userImpl.setLastKnownName(user.getName());
                dao.saveUser(userImpl);
            });
        }));
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
    public @Nullable Class<UserConnectEvent> getType() {
        return UserConnectEvent.class;
    }
}
