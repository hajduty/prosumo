package me.hajder.testplugin.command.commands;

import me.hajder.testplugin.SumoPlugin;
import me.hajder.testplugin.listener.SumoEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.hajder.testplugin.util.Utils.colorComponent;

public final class SumoDebugCommand  {
    public static void DebugCommand(CommandSender sender, String[] args) {
        SumoEvent sumoEvent = SumoEvent.getInstance(SumoPlugin.getInstance());
        sender.sendMessage(colorComponent(sumoEvent.getDebug()));
    }
}
