package me.alex.coinsapi.api;

import java.util.UUID;

public interface CoinUser {

    /**
     * Gets the UUID of the player
     *
     * @return UUID
     */
    UUID getUniqueId();

    /**
     * Gets the coins of the player
     *
     * @return Long
     */
    Long getCoins();

    /**
     * Sets the coins of the player
     *
     * @param coins The amount of coins
     */
    void setCoins(Long coins);

    /**
     * Gets the last known name of the player
     *
     * @return String
     */
    String getLastKnownName();

    /**
     * Gets the multiplier of the player
     *
     * @return double
     */
    double getMultiplier();

    /**
     * Sets the multiplier of the player
     *
     * @param multiplier The multiplier
     */
    void setMultiplier(double multiplier);

    /**
     * Adds coins to the player. The multiplier will be applied. It uses {@link Math#round} to round the coins.
     *
     * @param coins The amount of coins
     */
    void addCoins(Long coins);

    /**
     * Removes coins from the player
     *
     * @param coins The amount of coins
     */
    void removeCoins(Long coins);
}
