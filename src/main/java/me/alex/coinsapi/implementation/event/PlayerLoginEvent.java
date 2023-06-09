package me.alex.coinsapi.implementation.event;

import dev.hypera.chameleon.event.EventSubscriber;
import dev.hypera.chameleon.event.EventSubscriptionPriority;
import dev.hypera.chameleon.event.common.UserConnectEvent;
import dev.hypera.chameleon.user.User;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j(topic = "CoinsAPI")
public class PlayerLoginEvent implements EventSubscriber<UserConnectEvent> {
    private final CoinUserDAO dao;
    private final CoinsUserCache cache;

    public PlayerLoginEvent(CoinsAPI plugin) {
        this.dao = plugin.getDatabase();
        this.cache = plugin.getCache();

        plugin.getChameleon().getEventBus().subscribe(this);
    }

    @Override
    public void on(@NotNull UserConnectEvent event) throws Exception {
        User user = event.getUser();

        CompletableFuture<Optional<CoinUser>> future = dao.getUserAsync(user.getId());
        future.whenComplete((coinUser, throwable) -> {
            if (throwable != null) {
                event.setCancelled(true, getErrorMessage(throwable.getLocalizedMessage()));
                log.error("Error while getting user {} from database", user.getName(), throwable);
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

            isSuccess = cache.load(user.getId()) && isSuccess;

            if (!isSuccess) {
                event.setCancelled(true, getErrorMessage("Error while caching user"));
                log.warn("Error while caching user {}", user.getName());
            }
        });

    }

    @Override
    public @NotNull EventSubscriptionPriority getPriority() {
        return EventSubscriptionPriority.HIGH;
    }

    @Override
    public @Nullable Class<UserConnectEvent> getType() {
        return UserConnectEvent.class;
    }


    private Component getErrorMessage(String message) {
        return Messages.PREFIX.append(Component
                .translatable("coinsapi.error")
                .args(Component.text(message)));
    }
}
