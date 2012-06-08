package com.probablycoding.bukkit.cooldown;

import org.bukkit.configuration.file.FileConfiguration;

public class Configuration {
    final private Cooldown plugin;

    // Length of cooldown for all users, applied on join
    public static int COOLDOWN_TICKS;

    // Radius in blocks to look for nearby enemies
    public static int ENEMY_RADIUS;
    // Length of cooldown to apply on quit if enemies are nearby
    public static int RADIUS_COOLDOWN_TICKS;

    // Time in milliseconds after player takes damage to apply a cooldown
    public static int DAMAGE_TIME;
    // Length of cooldown to apply on quit if player was recently damaged
    public static int DAMAGE_COOLDOWN_TICKS;

    public Configuration(Cooldown instance) {
        plugin = instance;
        loadStatics();
    }

    public void reload() {
        plugin.reloadConfig();
        loadStatics();
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();
        config.set("cooldown-ticks", COOLDOWN_TICKS);
        config.set("enemy-radius", ENEMY_RADIUS);
        config.set("radius-cooldown-ticks", RADIUS_COOLDOWN_TICKS);
        config.set("damage-time", DAMAGE_TIME);
        config.set("damage-cooldown-ticks", DAMAGE_COOLDOWN_TICKS);
        plugin.saveConfig();
    }

    private void loadStatics() {
        FileConfiguration config = plugin.getConfig();

        COOLDOWN_TICKS        = config.getInt("cooldown-ticks");
        ENEMY_RADIUS          = config.getInt("enemy-radius");
        RADIUS_COOLDOWN_TICKS = config.getInt("radius-cooldown-ticks");
        DAMAGE_TIME           = config.getInt("damage-time");
        DAMAGE_COOLDOWN_TICKS = config.getInt("damage-cooldown-ticks");
    }
}
