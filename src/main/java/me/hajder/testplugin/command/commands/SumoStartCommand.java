package me.hajder.testplugin.command.commands;

import me.hajder.testplugin.SumoPlugin;
import me.hajder.testplugin.listener.SumoEvent;
import me.hajder.testplugin.settings.SumoSettings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.hajder.testplugin.util.Utils.colorComponent;

public final class SumoStartCommand {
    public static void StartCommand(CommandSender sender, String[] args) {
        SumoEvent sumoEvent = SumoEvent.getInstance(SumoPlugin.getInstance());

        if (!sumoEvent.sumoProgress()) {
            sumoEvent.setupSumo();
        } else {
            sender.sendMessage(colorComponent((String) SumoSettings.getInstance().get("messages.sumoAlreadyInProgress")));
        }
    }
}