package me.alex.coinsapi.implementation.event;

import dev.hypera.chameleon.event.EventSubscriber;
import dev.hypera.chameleon.event.EventSubscriptionPriority;
import dev.hypera.chameleon.event.common.UserConnectEvent;
import dev.hypera.chameleon.logger.ChameleonLogger;
import dev.hypera.chameleon.user.User;
import me.alex.coinsapi.api.CoinUser;
import me.alex.coinsapi.api.CoinUserDAO;
import me.alex.coinsapi.api.CoinsUserCache;
import me.alex.coinsapi.implementation.CoinsAPI;
import me.alex.coinsapi.implementation.data.Messages;
import me.alex.coinsapi.implementation.data.pojo.UserImpl;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PlayerLoginEvent implements EventSubscriber<UserConnectEvent> {
    private final ChameleonLogger logger;
    private final CoinUserDAO dao;
    private final CoinsUserCache cache;

    public PlayerLoginEvent(CoinsAPI plugin) {
        this.logger = plugin.getChameleon().getLogger();
        this.dao = plugin.getDatabase();
        this.cache = plugin.getApi().getCache();

        plugin.getChameleon().getEventBus().subscribe(this);
    }

    @Override
    public void on(@NotNull UserConnectEvent event) throws Exception {
        User user = event.getUser();

        CompletableFuture<Optional<CoinUser>> future = dao.getUserAsync(user.getId());
        future.whenComplete((coinUser, throwable) -> {
            if (throwable != null) {
                event.cancel(getErrorMessage());
                logger.info("Error while getting user {} from database", user.getName(), throwable);
                return;
            }

            //Check if user is present in database.
            // needSave is true if user is not present in database or if user's name is different from last known name.
            // isSuccess is true if user is saved/edited successfully.
            boolean needSave = false, isSuccess = true;
            CoinUser newUser = null;
            if (coinUser.isEmpty()) {
                //Create new user
                newUser = new UserImpl(user.getId(), 0L, user.getName(), 0D);
                needSave = true;
            }

            if (coinUser.isPresent()) {
                UserImpl userImpl = (UserImpl) coinUser.get();
                if (!userImpl.getLastKnownName().equals(user.getName())) {
                    userImpl.setLastKnownName(user.getName());
                    needSave = true;
                }
            }

            if (needSave) {
                isSuccess = dao.saveUser(newUser);
            }

            isSuccess = cache.load(user.getId());

            if (!isSuccess) {
                event.cancel(getErrorMessage());
                logger.info("Error while saving/editing/caching user {} to/from database", user.getName());
            }
        });

    }

    @Override
    public @NotNull EventSubscriptionPriority getPriority() {
        return EventSubscriptionPriority.MEDIUM;
    }

    @Override
    public @Nullable Class<UserConnectEvent> getType() {
        return UserConnectEvent.class;
    }

    private Component getErrorMessage() {
        return Messages.PREFIX.append(Component.translatable("coinsapi.error"));
    }
}
