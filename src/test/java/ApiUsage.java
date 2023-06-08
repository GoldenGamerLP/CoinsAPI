import me.alex.coinsapi.api.CoinAPI;
import me.alex.coinsapi.api.CoinAPIProvider;
import me.alex.coinsapi.api.CoinUser;

import java.util.Optional;
import java.util.UUID;

public class ApiUsage {

    public void example() {
        // Get the API instance, can throw an exception if the API is not loaded
        CoinAPI api = CoinAPIProvider.getInstance();

        // The API is now ready to use and you can use it like this:
        UUID uuid = UUID.randomUUID();

        // Get the CoinUser from the database
        Optional<CoinUser> optional = api.getUser(uuid);
        if (optional.isPresent()) {
            CoinUser user = optional.get();
            // Do something with the user
        } else {
            // The user does not exist in the database
        }

        api.hasUser(uuid); // Checks if the user exists in the database, returns a boolean

        api.editUser(uuid, coinUser -> {
            //Edit the user here, when done the user is saved automatically. Everything here is async.
            coinUser.setMultiplier(2.0);
            //Important: The Multiplier is used to addCoins.
            coinUser.addCoins(1000L);
        });
    }
}
