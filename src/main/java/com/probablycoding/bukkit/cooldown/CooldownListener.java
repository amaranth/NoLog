package com.probablycoding.bukkit.cooldown;

import java.util.HashMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDisconnectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerReconnectEvent;

public class CooldownListener implements Listener {
    final private Cooldown plugin;
    final private HashMap<String,Long> damageTimes = new HashMap<String,Long>();

    public CooldownListener(Cooldown instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setLogoutCooldownTicks(Configuration.COOLDOWN_TICKS);
    }

    @EventHandler
    public void onPlayerReconnect(PlayerReconnectEvent event) {
        plugin.getLogger().info(event.getPlayer().getName() + " joined on cooldown.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final String name = event.getPlayer().getName();
        damageTimes.remove(name);
        plugin.getLogger().info(name + " left the game for real.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            final Player player = (Player) event.getEntity();
            damageTimes.put(player.getName(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        damageTimes.remove(event.getEntity().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("cooldown.immune")) {
            player.setLogoutCooldownTicks(0);
            plugin.getLogger().info(player.getName() + " is immune, cancelled cooldown.");
            return;
        }

        // Look for nearby players
        if (Configuration.RADIUS_COOLDOWN_TICKS > 0) {
            int radius = Configuration.ENEMY_RADIUS;
            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                if (entity.getType() == EntityType.PLAYER) {
                    player.setLogoutCooldownTicks(Configuration.RADIUS_COOLDOWN_TICKS);
                    break;
                }
            }
        }

        // See if this player has recently taken damage
        if (Configuration.DAMAGE_COOLDOWN_TICKS > 0) {
            Long damageTime = damageTimes.get(player.getName());
            if (damageTime != null && System.currentTimeMillis() - damageTime < Configuration.DAMAGE_TIME) {
                player.setLogoutCooldownTicks(Configuration.DAMAGE_COOLDOWN_TICKS);
            }
        }

        if (player.getLogoutCooldownTicks() > 0) {
            plugin.getLogger().info(player.getName() + " is about to be on cooldown for " + player.getLogoutCooldownTicks() + " ticks.");
        }
    }
}
