package me.hajder.testplugin;

import me.hajder.testplugin.command.CommandHandler;
import me.hajder.testplugin.command.commands.SumoAddCommand;
import me.hajder.testplugin.command.commands.SumoDebugCommand;
import me.hajder.testplugin.command.commands.SumoSetCommand;
import me.hajder.testplugin.command.commands.SumoStartCommand;
import me.hajder.testplugin.listener.SumoEvent;
import me.hajder.testplugin.settings.SumoSettings;
import org.bukkit.plugin.java.JavaPlugin;

public final class SumoPlugin extends JavaPlugin {
    private static SumoPlugin instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getLogger().info("Professional sumo plugin enabled!");
        getCommand("sumo").setExecutor(new CommandHandler());

        SumoEvent sumoEvent = SumoEvent.getInstance(this);
        getServer().getPluginManager().registerEvents(sumoEvent, this);
        SumoSettings.getInstance().load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Bye from sumo plugin");
    }

    public static SumoPlugin getInstance()
    {
        return getPlugin(SumoPlugin.class);
    }
}
