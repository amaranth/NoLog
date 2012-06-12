package com.probablycoding.bukkit.nolog;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class NoLog extends JavaPlugin {
    public Configuration config;

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "NoLog Commands: (/nolog <command>)");
        sender.sendMessage(ChatColor.GOLD + "  help " + ChatColor.GRAY + "- " + ChatColor.RESET + "Show this help message");
        sender.sendMessage(ChatColor.GOLD + "  info " + ChatColor.GRAY + "- " + ChatColor.RESET + "Show current settings");
        sender.sendMessage(ChatColor.GOLD + "  reload " + ChatColor.GRAY + "- " + ChatColor.RESET + "Reload configuration from file");
        sender.sendMessage(ChatColor.GOLD + "  default [ticks] " + ChatColor.GRAY + "- " + ChatColor.RESET + "Set ticks for general cooldown");
        sender.sendMessage(ChatColor.GOLD + "  radius [radius] " + ChatColor.GRAY + "- " + ChatColor.RESET + "Set radius in blocks to look for other players");
        sender.sendMessage(ChatColor.GOLD + "  radius-cooldown [ticks] " + ChatColor.GRAY + "- " + ChatColor.RESET + "Set ticks for radius cooldown");
        sender.sendMessage(ChatColor.GOLD + "  damage-timeout [time] " + ChatColor.GRAY + "- " + ChatColor.RESET + "Set time in milliseconds to enforce damage cooldown");
        sender.sendMessage(ChatColor.GOLD + "  damage-cooldown [ticks] " + ChatColor.GRAY + "- " + ChatColor.RESET + "Set ticks for damage cooldown");
        sender.sendMessage(ChatColor.GOLD + "  damage-only-pvp [true|false] " + ChatColor.GRAY + "- " + ChatColor.RESET + "Set whether damage cooldown only applies to PVP or not");
    }

    public void onEnable() {
        config = new Configuration(this);
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new NoLogListener(this), this);
    }

    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (command.getName().equalsIgnoreCase("nolog")) {
            if (args.length == 0) {
                sendHelpMessage(sender);
                return true;
            }

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("info")) {
                    sender.sendMessage("NoLog Status:");
                    sender.sendMessage("  Default - " + Configuration.COOLDOWN_TICKS + " ticks");
                    sender.sendMessage("  Radius - " + Configuration.ENEMY_RADIUS + " blocks");
                    sender.sendMessage("  Radius Cooldown - " + Configuration.RADIUS_COOLDOWN_TICKS + " ticks");
                    sender.sendMessage("  Damage Timeout - " + Configuration.DAMAGE_TIME + " milliseconds");
                    sender.sendMessage("  Damage Cooldown - " + Configuration.DAMAGE_COOLDOWN_TICKS + " ticks");
                    sender.sendMessage("  Damage Only PVP - " + Configuration.DAMAGE_ONLY_PVP);
                } else if (args[0].equalsIgnoreCase("reload")) {
                    config.reload();
                    sender.sendMessage("NoLog configuration reloaded.");
                } else {
                    sendHelpMessage(sender);
                }

                return true;
            }

            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("damage-only-pvp")) {
                    boolean result = args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("true");
                    Configuration.DAMAGE_ONLY_PVP = result;
                    config.save();
                    if (result) {
                        sender.sendMessage("Damage cooldown will only apply to PVP.");
                    } else {
                        sender.sendMessage("Damage cooldown will apply to all damage.");
                    }
                    return true;
                }

                int number;
                try {
                    number = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    sender.sendMessage("Failed to parse argument, did you provide a number?");
                    return true;
                }

                if (args[0].equalsIgnoreCase("default")) {
                    Configuration.COOLDOWN_TICKS = number;
                    config.save();
                    sender.sendMessage("Default cooldown set to " + Configuration.COOLDOWN_TICKS + " ticks.");
                } else if (args[0].equalsIgnoreCase("radius")) {
                    Configuration.ENEMY_RADIUS = number;
                    config.save();
                    sender.sendMessage("Enemy radius set to " + Configuration.ENEMY_RADIUS + " blocks.");
                } else if (args[0].equalsIgnoreCase("radius-cooldown")) {
                    Configuration.RADIUS_COOLDOWN_TICKS = number;
                    config.save();
                    sender.sendMessage("Radius cooldown set to " + Configuration.RADIUS_COOLDOWN_TICKS + " ticks.");
                } else if (args[0].equalsIgnoreCase("damage-timeout")) {
                    Configuration.DAMAGE_TIME = number;
                    config.save();
                    sender.sendMessage("Damage timeout set to " + Configuration.DAMAGE_TIME + " milliseconds.");
                } else if (args[0].equalsIgnoreCase("damage-cooldown")) {
                    Configuration.DAMAGE_COOLDOWN_TICKS = number;
                    config.save();
                    sender.sendMessage("Damage cooldown set to " + Configuration.DAMAGE_COOLDOWN_TICKS + " ticks.");
                } else {
                    sendHelpMessage(sender);
                }
            }
        }

        return true;
    }
}
