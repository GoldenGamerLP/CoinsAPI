package me.alex.coinsapi.api;

import org.jetbrains.annotations.Blocking;

import java.util.List;
import java.util.UUID;

public interface CoinsUserCache {

    /**
     * Invalidates/Removes the CoinUser from the cache and saves the current data to the database.
     *
     * @param uuid The UUID of the player
     */
    @Blocking
    void invalidate(UUID uuid);

    /**
     * Invalidates/Removes every CoinUser from the cache and saves the current data to the database.
     */
    @Blocking
    void invalidateAll();

    /**
     * Loads a CoinUser from the database and caches it.
     *
     * @param uuids The UUIDs of the players
     */
    @Blocking
    boolean loadAll(List<UUID> uuids);

    /**
     * Loads a CoinUser from the database and caches it.
     *
     * @param uuid The UUID of the player
     */
    @Blocking
    default boolean load(UUID uuid) {
        return loadAll(List.of(uuid));
    }
}
