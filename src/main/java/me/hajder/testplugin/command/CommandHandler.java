package me.hajder.testplugin.command;

import me.hajder.testplugin.command.commands.SumoAddCommand;
import me.hajder.testplugin.command.commands.SumoDebugCommand;
import me.hajder.testplugin.command.commands.SumoSetCommand;
import me.hajder.testplugin.command.commands.SumoStartCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.hajder.testplugin.util.Utils.colorComponent;

public class CommandHandler implements TabExecutor, CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sender.sendMessage(colorComponent(helpMessage()));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch(subCommand) {
            case "forceadd":
                SumoAddCommand.AddCommand(sender,args);
                return true;
            case "start":
                SumoStartCommand.StartCommand(sender, args);
                return true;
            case "set":
                SumoSetCommand.SetCommand(sender,args);
                return true;
            case "debug":
                SumoDebugCommand.DebugCommand(sender,args);
                return true;
            default:
                sender.sendMessage(colorComponent(helpMessage()));
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String subCommand = args[0].toLowerCase();
        switch(subCommand) {
            case "forceadd":
            case "start":
            case "debug":
                return null;
            case "set":
                return new ArrayList<>(Arrays.asList("0", "1", "2"));
            default:
                break;
        }
        return new ArrayList<>(Arrays.asList("forceadd", "start", "set", "debug"));
    }

    private String helpMessage() {
        String msg = "";

        msg += "ProSumo v0001\n";
        msg += "commands: \n";
        msg += "  /sumo start - Starts sumo event\n";
        msg += "  /sumo forceadd (player) - Adds eliminated player to current round\n";
        msg += "  /sumo set (pos) - Sets spawn point for world, p1, p2\n";
        msg += "  /sumo debug - Debug info\n";

        return msg;
    }
}
