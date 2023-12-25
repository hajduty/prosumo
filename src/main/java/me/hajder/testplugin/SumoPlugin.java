package me.hajder.testplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class SumoPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Hello from TestPlugin");
        getCommand("sumo-start").setExecutor(new SumoStartCommand());
        getCommand("sumo-set").setExecutor(new SumoSetCommand());
        getServer().getPluginManager().registerEvents(new SumoEvent(this), this);
        SumoSettings.getInstance().load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Bye from TestPlugin");
    }

    public static SumoPlugin getInstance()
    {
        return getPlugin(SumoPlugin.class);
    }
}
