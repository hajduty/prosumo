package me.hajder.testplugin;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SumoSetCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        sender.sendMessage(args.length + "");

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only men can use this command");
            return true;
        }

        if (args.length >= 1) {
            Player player = (Player) sender;
            String settingPath;
            String message;

            switch (args[0]) {
                case "0":
                    settingPath = "sumo-cords.spawn";
                    message = "Set sumo world spawn to: ";
                    break;
                case "1":
                    settingPath = "sumo-cords.player1";
                    message = "Set first sumo pos to: ";
                    break;
                case "2":
                    settingPath = "sumo-cords.player2";
                    message = "Set second sumo pos to: ";
                    break;
                default:
                    sender.sendMessage("Bad usage: /sumo set (int)");
                    return true;
            }

            Location location = player.getLocation();
            SumoSettings.getInstance().set(settingPath + ".X", location.getX());
            SumoSettings.getInstance().set(settingPath + ".Y", location.getY());
            SumoSettings.getInstance().set(settingPath + ".Z", location.getZ());
            SumoSettings.getInstance().set(settingPath + ".World", location.getWorld().getName());

            sender.sendMessage(message + location.getX() + "-" + location.getY() + "-" + location.getZ() + " in world: " + location.getWorld().getName());
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> g = new ArrayList<>();

        if (args.length == 1) {
            g.add("0");
            g.add("1");
            g.add("2");
        }

        return g;
    }
}
