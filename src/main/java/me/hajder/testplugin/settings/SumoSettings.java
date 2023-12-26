package me.hajder.testplugin.settings;

import me.hajder.testplugin.SumoPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class SumoSettings {
    private final static SumoSettings instance = new SumoSettings();
    private File file;
    private YamlConfiguration config;

    private SumoSettings() {
    }

    public void load() {
        file = new File(SumoPlugin.getInstance().getDataFolder(), "settings.yml");

        if (!file.exists())
            SumoPlugin.getInstance().saveResource("settings.yml", false);

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void save()
    {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object value)
    {
        config.set(path, value);

        save();
    }

    public Object get(String path) {
        return config.get(path);
    }

    public static SumoSettings getInstance() {
        return instance;
    }
}
