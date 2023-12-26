package me.hajder.testplugin.command.commands;

import me.hajder.testplugin.SumoPlugin;
import me.hajder.testplugin.listener.SumoEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.hajder.testplugin.util.Utils.colorComponent;

public final class SumoAddCommand  {
    public static void AddCommand( CommandSender sender, String[] args) {
        SumoEvent sumoEvent = SumoEvent.getInstance(SumoPlugin.getInstance());
        String result = sumoEvent.forceAdd(sender.getServer().getPlayer(args[1]));
        sender.sendMessage(colorComponent(result));
    }
}
