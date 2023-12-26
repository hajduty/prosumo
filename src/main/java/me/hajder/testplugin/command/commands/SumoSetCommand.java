package me.hajder.testplugin.command.commands;

import me.hajder.testplugin.settings.SumoSettings;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SumoSetCommand {
    public static void SetCommand( CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only men can use this command");
        }

        if (args.length >= 1) {
            Player player = (Player) sender;
            String settingPath;
            String message;

            switch (args[1]) {
                case "0":
                    settingPath = "sumo-cords.spawn";
                    message = "Set sumo world spawn to: \n";
                    break;
                case "1":
                    settingPath = "sumo-cords.player1";
                    message = "Set first sumo pos to: \n";
                    break;
                case "2":
                    settingPath = "sumo-cords.player2";
                    message = "Set second sumo pos to: \n";
                    break;
                default:
                    sender.sendMessage("Bad usage: /sumo set (int)");
                    return;
            }

            Location location = player.getLocation();
            SumoSettings.getInstance().set(settingPath + ".X", location.getX());
            SumoSettings.getInstance().set(settingPath + ".Y", location.getY());
            SumoSettings.getInstance().set(settingPath + ".Z", location.getZ());
            SumoSettings.getInstance().set(settingPath + ".World", location.getWorld().getName());
            SumoSettings.getInstance().set(settingPath + ".Yaw", player.getYaw());
            SumoSettings.getInstance().set(settingPath + ".Pitch", player.getPitch());

            sender.sendMessage(message + location.getX() + "\n" + location.getY() + "\n" + location.getZ() + "\n in world: " + location.getWorld().getName());
        }
    }
}
