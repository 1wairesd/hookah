package org.jedels.hookah;

import org.bukkit.plugin.java.JavaPlugin;
import org.jedels.hookah.events.HookahEvent;

public final class Plugin extends JavaPlugin {

    private Database database;

    @Override
    public void onEnable() {
        Controller.setup(this);
        database = Database.getInstance();
        getServer().getPluginManager().registerEvents(new HookahEvent(), this);
    }

    @Override
    public void onDisable() {
        if (database != null) database.save();
    }
}
