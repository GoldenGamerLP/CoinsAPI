package me.alex.coinsapi.implementation.platform.bungeecord;

import dev.hypera.chameleon.ChameleonPluginData;
import dev.hypera.chameleon.exception.ChameleonRuntimeException;
import dev.hypera.chameleon.exception.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platform.bungeecord.BungeeCordChameleon;
import java.lang.Override;
import java.util.Arrays;
import java.util.logging.Level;
import me.alex.coinsapi.implementation.CoinsAPI;
import net.md_5.bungee.api.plugin.Plugin;

public class CoinsAPIBungeeCord extends Plugin {
    private BungeeCordChameleon chameleon;

    @Override
    public void onLoad() {
        try {
            ChameleonPluginData pluginData = ChameleonPluginData.builder("CoinsAPI", "@version@").description("CoinsAPI").url("").authors(Arrays.asList("Alex")).build();
            this.chameleon = BungeeCordChameleon.create(CoinsAPI.class, this, pluginData).load();
        } catch (ChameleonInstantiationException ex) {
            getLogger().log(Level.SEVERE, "An error occurred while loading Chameleon", ex);
            throw new ChameleonRuntimeException(ex);
        }
    }

    @Override
    public void onEnable() {
        this.chameleon.onEnable();
    }

    @Override
    public void onDisable() {
        this.chameleon.onDisable();
    }
}
