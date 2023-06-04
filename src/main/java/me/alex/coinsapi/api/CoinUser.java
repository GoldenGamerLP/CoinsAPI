package me.alex.coinsapi.api;

import java.util.UUID;

public interface CoinUser {

    UUID getUniqueId();

    Long getCoins();

    String getLastKnownName();

    double getMultiplier();

    void setCoins(Long coins);

    void addCoins(Long coins);

    void removeCoins(Long coins);
}
