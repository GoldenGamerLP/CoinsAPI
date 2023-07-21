package me.alex.coinsapi.implementation.data.pojo;

import lombok.Data;
import lombok.Getter;
import me.alex.coinsapi.api.CoinUser;

import java.util.UUID;

@Data
public class UserImpl implements CoinUser {

    private final UUID uuid;
    private volatile Long coins;
    private String lastKnownName;
    private long lastSaved;
    @Getter
    private volatile double multiplier;

    public UserImpl(UUID uuid, Long coins, String lastKnownName, double multiplier) {
        this.uuid = uuid;
        this.coins = coins;
        this.lastKnownName = lastKnownName;
        this.multiplier = multiplier;
        this.lastSaved = System.currentTimeMillis();
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Long getCoins() {
        return coins;
    }

    @Override
    public void setCoins(Long coins) {
        this.coins = coins;
    }

    @Override
    public long addCoins(Long coins) {
        long multi = Math.round(coins * multiplier);
        this.coins += multi;
        return multi;
    }

    @Override
    public boolean removeCoins(Long coins) {
        if (!hasEnoughCoins(coins)) return false;
        this.coins -= coins;
        return true;
    }

    @Override
    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }

    @Override
    public boolean hasMultiplier() {
        return multiplier != 1;
    }

    @Override
    public boolean hasEnoughCoins(Long coins) {
        return this.coins >= coins;
    }

    public long getLastSaved() {
        return lastSaved;
    }

    public void setLastSaved(long lastSaved) {
        this.lastSaved = lastSaved;
    }
}
