package me.hajder.testplugin.util;

import me.hajder.testplugin.SumoPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class Utils {
    public static void spawnFirework(Location location, Color color, Color fadeColor) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(color)
                .withFade(fadeColor)
                .with(FireworkEffect.Type.BALL)
                .trail(true)
                .build();

        fireworkMeta.addEffect(effect);
        firework.setFireworkMeta(fireworkMeta);

        // Schedule the firework to be removed after 2 seconds (40 ticks)
        Bukkit.getScheduler().runTaskLater(SumoPlugin.getInstance(), () -> firework.detonate(), 40L);
    }

    public static Component colorComponent(String string) { // smulan4 code baxning
        String serializedString = PlainTextComponentSerializer.plainText().serialize(Component.text(string));
        return MiniMessage.miniMessage().deserialize(serializedString);
    }
}
