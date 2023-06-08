package me.alex.coinsapi.api;

import java.util.List;
import java.util.UUID;

public interface CoinsUserCache {

    /**
     * Invalidates/Removes the CoinUser from the cache and saves the current data to the database.
     *
     * @param uuid The UUID of the player
     */
    void invalidate(UUID uuid);

    /**
     * Invalidates/Removes every CoinUser from the cache and saves the current data to the database.
     */


    /**
     * Loads a CoinUser from the database and caches it.
     *
     * @param uuids The UUIDs of the players
     */
    boolean loadAll(List<UUID> uuids);

    /**
     * Loads a CoinUser from the database and caches it.
     *
     * @param uuid The UUID of the player
     */
    default boolean load(UUID uuid) {
        return loadAll(List.of(uuid));
    }

    void invalidateAll();
}
