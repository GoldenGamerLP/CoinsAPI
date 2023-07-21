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
     * Sets the last known name of the player
     *
     * @param lastKnownName The last known name
     */
    void setLastKnownName(String lastKnownName);

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
     * @return The amount of coins that were added
     */
    long addCoins(Long coins);

    /**
     * Removes coins from the player
     *
     * @param coins The amount of coins
     * @return boolean if the player has enough coins
     */
    boolean removeCoins(Long coins);

    /**
     * Checks if the player has a multiplier.
     *
     * @return boolean
     */
    boolean hasMultiplier();

    /**
     * Checks if the player has enough coins.
     *
     * @param coins The amount of coins
     * @return boolean
     */
    boolean hasEnoughCoins(Long coins);

    /**
     * Gets the last saved time of the player
     *
     * @return long
     */
    long getLastSaved();

    /**
     * Sets the last saved time of the player
     *
     * @param lastSaved The last saved time
     */
    void setLastSaved(long lastSaved);
}
