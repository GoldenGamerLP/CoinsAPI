package me.alex.coinsapi.implementation.platform.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.hypera.chameleon.ChameleonPluginData;
import dev.hypera.chameleon.exception.ChameleonRuntimeException;
import dev.hypera.chameleon.exception.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platform.velocity.VelocityChameleon;
import dev.hypera.chameleon.platform.velocity.VelocityPlugin;
import java.lang.Override;
import java.nio.file.Path;
import java.util.Arrays;
import me.alex.coinsapi.implementation.CoinsAPI;
import org.slf4j.Logger;

@Plugin(
        id = "coinsapi",
        name = "CoinsAPI",
        version = "@version@",
        description = "CoinsAPI",
        url = "",
        authors = "Alex"
)
public class CoinsAPIVelocity implements VelocityPlugin {
    private final ProxyServer server;

    private final Logger logger;

    private final Path dataDirectory;

    private VelocityChameleon chameleon;

    @Inject
    public CoinsAPIVelocity(Logger logger, ProxyServer server, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        try {
            ChameleonPluginData pluginData = ChameleonPluginData.builder("CoinsAPI", "@version@").description("CoinsAPI").url("").authors(Arrays.asList("Alex")).build();
            this.chameleon = VelocityChameleon.create(CoinsAPI.class, this, pluginData).load();
        } catch (ChameleonInstantiationException ex) {
            this.logger.error("An error occurred while loading Chameleon", ex);
            throw new ChameleonRuntimeException(ex);
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.chameleon.onEnable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.chameleon.onDisable();
    }

    @Override
    public ProxyServer getServer() {
        return this.server;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public Path getDataDirectory() {
        return this.dataDirectory;
    }
}
