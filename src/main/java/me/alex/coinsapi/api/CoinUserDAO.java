package me.alex.coinsapi.api;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CoinUserDAO {

    void connect();

    void disconnect();

    boolean saveUser(CoinUser user);

    boolean deleteUser(CoinUser user);

    Optional<CoinUser> getUser(UUID uuid);

    boolean hasUser(UUID uuid);

    CompletableFuture<Boolean> saveUserAsync(CoinUser user);

    CompletableFuture<Boolean> deleteUserAsync(CoinUser user);

    CompletableFuture<Optional<CoinUser>> getUserAsync(UUID uuid);
}
