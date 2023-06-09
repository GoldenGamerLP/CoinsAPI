package me.alex.coinsapi.implementation.platform.bukkit;

import dev.hypera.chameleon.ChameleonPluginData;
import dev.hypera.chameleon.exception.ChameleonRuntimeException;
import dev.hypera.chameleon.exception.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platform.bukkit.BukkitChameleon;
import java.lang.Override;
import java.util.Arrays;
import java.util.logging.Level;
import me.alex.coinsapi.implementation.CoinsAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class CoinsAPIBukkit extends JavaPlugin {
    private BukkitChameleon chameleon;

    public CoinsAPIBukkit() {
        try {
            ChameleonPluginData pluginData = ChameleonPluginData.builder("CoinsAPI", "@version@").description("CoinsAPI").url("").authors(Arrays.asList("Alex")).build();
            this.chameleon = BukkitChameleon.create(CoinsAPI.class, this, pluginData).load();
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
