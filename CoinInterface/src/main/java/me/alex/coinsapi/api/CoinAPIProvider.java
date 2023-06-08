package me.alex.coinsapi.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.AvailableSince("1.0.0")
public class CoinAPIProvider {

    private static volatile CoinAPI instance;

    /**
     * Gets the instance of the CoinAPIProvider
     *
     * @return the instance of the CoinAPIProvider
     * @throws IllegalStateException if the instance has not been set yet
     */
    @NotNull
    public static CoinAPI getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CoinAPIProvider has not been set yet!");
        }

        return instance;
    }

    public static void setInstance(CoinAPI instance) {
        CoinAPIProvider.instance = instance;
    }
}
