package me.alex.coinsapi.implementation.data.pojo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.alex.coinsapi.api.CoinUser;

import java.util.UUID;

@Data
public class UserImpl implements CoinUser {

    private final UUID uuid;
    private volatile Long coins;
    private String lastKnownName;
    @Getter
    private volatile double multiplier;

    public UserImpl(UUID uuid, Long coins, String lastKnownName, double multiplier) {
        this.uuid = uuid;
        this.coins = coins;
        this.lastKnownName = lastKnownName;
        this.multiplier = multiplier;
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
    public void addCoins(Long coins) {
        this.coins += coins;
    }

    @Override
    public void removeCoins(Long coins) {
        this.coins -= coins;
    }

    @Override
    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }
}
