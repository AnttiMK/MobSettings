package dev.kopo.mobsettings;

import dev.kopo.mobsettings.commands.MobSettingsCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MobSettings extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("mobsettings").setExecutor(new MobSettingsCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
