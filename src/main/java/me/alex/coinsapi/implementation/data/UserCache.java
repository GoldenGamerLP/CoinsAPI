package me.alex.coinsapi.implementation.data;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import me.alex.coinsapi.api.CoinUser;
import me.alex.coinsapi.api.CoinUserDAO;
import me.alex.coinsapi.implementation.CoinsAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UserCache implements CoinUserDAO {

    @Getter
    private final Cache<UUID, CoinUser> cache;
    private final MongoDBImpl mongoDB;

    public UserCache(@NotNull CoinsAPI plugin) {
        this.mongoDB = plugin.getDatabase();
        cache = Caffeine.newBuilder()
                .softValues()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .removalListener((key, value, cause) -> mongoDB.saveUserAsync((CoinUser) value))
                .build();
    }

    @Override
    public void connect() {
        mongoDB.connect();
    }

    @Override
    public void disconnect() {
        mongoDB.disconnect();
    }

    @Override
    public boolean saveUser(@NotNull CoinUser user) {
        CoinUser cached = cache.getIfPresent(user.getUniqueId());
        if (cached != null) mongoDB.saveUser(cached);
        return mongoDB.saveUser(user);
    }

    @Override
    public boolean deleteUser(CoinUser user) {
        return mongoDB.deleteUser(user);
    }

    @Override
    public Optional<CoinUser> getUser(UUID uuid) {
        CoinUser user = cache.getIfPresent(uuid);
        if (user != null) return Optional.of(user);

        Optional<CoinUser> optional = mongoDB.getUser(uuid);
        optional.ifPresent(value -> cache.put(value.getUniqueId(), value));
        return optional;
    }

    @Override
    public Optional<CoinUser> getUser(String name) {
        CoinUser user = cache.asMap().values().stream()
                .filter(User -> User.getLastKnownName().equals(name))
                .findFirst()
                .orElse(null);

        if (user != null) return Optional.of(user);

        Optional<CoinUser> optional = mongoDB.getUser(name);
        optional.ifPresent(value -> cache.put(value.getUniqueId(), value));
        return optional;
    }

    @Override
    public boolean hasUser(UUID uuid) {
        return mongoDB.hasUser(uuid);
    }

    @Override
    public CompletableFuture<Boolean> saveUserAsync(CoinUser user) {
        return mongoDB.saveUserAsync(user);
    }

    @Override
    public CompletableFuture<Boolean> deleteUserAsync(CoinUser user) {
        return mongoDB.deleteUserAsync(user);
    }

    @Override
    public CompletableFuture<Optional<CoinUser>> getUserAsync(UUID uuid) {
        return mongoDB.getUserAsync(uuid);
    }

    @Override
    public CompletableFuture<Optional<CoinUser>> getUserAsync(String name) {
        return mongoDB.getUserAsync(name);
    }
}
