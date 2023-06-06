package me.alex.coinsapi.implementation.event;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.event.EventSubscriber;
import dev.hypera.chameleon.event.EventSubscriptionPriority;
import dev.hypera.chameleon.event.common.UserConnectEvent;
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

    public PlayerLoginEvent(CoinsAPI plugin) {
        this.chameleon = plugin.getChameleon();
        this.dao = plugin.getDatabase();

        plugin.getChameleon().getEventBus().subscribe(this);
    }

    @Override
    public void on(@NotNull UserConnectEvent event) throws Exception {
        User user = event.getUser();

        System.out.println("User " + user.getName() + " connected");
        /*CompletableFuture<Optional<CoinUser>> future = dao.getUserAsync(user.getId());

        future.thenAccept(coinUser -> {

        });*/
        //TODO: Kick user if error occurs
        //TODO: Add Prefix
        //TODO: Check Return value, if false kick and say try again later
        Optional<CoinUser> optional = dao.getUser(user.getId());
        if (optional.isEmpty()) {
            CoinUser newUser = new UserImpl(user.getId(), 0L, user.getName(), 0D);
            System.out.println(dao.saveUser(newUser));
        }
        System.out.println("User " + user.getName() + " loaded");

        //Update name if needed
        optional.ifPresent(coinUser1 -> {
            System.out.println(coinUser1.getLastKnownName());
            UserImpl userImpl = (UserImpl) coinUser1;
            if (!userImpl.getLastKnownName().equals(user.getName())) return;

            userImpl.setLastKnownName(user.getName());
            System.out.println(dao.saveUser(userImpl));
        });
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
