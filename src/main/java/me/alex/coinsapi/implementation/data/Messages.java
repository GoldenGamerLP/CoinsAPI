package me.alex.coinsapi.implementation.data;

import dev.hypera.chameleon.Chameleon;
import me.alex.coinsapi.implementation.CoinsAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.minestom.server.adventure.MinestomAdventure;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {

    public static final Component PREFIX = Component.empty()
            .append(Component.text("Coins").color(TextColor.color(0xFF763B)))
            .append(Component.text(" | ").color(TextColor.color(0x434256)))
            .color(TextColor.color(0xA0AAA7));

    private static final String bundleName = "messages";
    private static final Locale[] locales = {
            new Locale("de", "DE"),
            new Locale("en", "US")
    };
    private static boolean isRegistered = false;

    public static void innit(CoinsAPI plugin) {
        if (isRegistered) throw new RuntimeException("You cannot register messages twice.");
        isRegistered = true;

        GlobalTranslator globalTranslator = GlobalTranslator.translator();
        TranslationRegistry registry = TranslationRegistry.create(Key.key("coins", "messages"));

        for (Locale locale : locales) {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
            registry.registerAll(locale, bundle, true);
        }

        globalTranslator.addSource(registry);

        enabledComponentTranslation(plugin);
    }

    private static void enabledComponentTranslation(CoinsAPI plugin) {
        Chameleon chameleon = plugin.getChameleon();
        switch (chameleon.getPlatform().getName()) {
            case "Minestom" -> MinestomAdventure.AUTOMATIC_COMPONENT_TRANSLATION = true;
            default ->  plugin.getLogger().warn(
                    "Automatic component translation may not be supported on this platform." +
                    "Report issues to the developer.");
        }
    }
}
