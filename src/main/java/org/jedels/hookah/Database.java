package org.jedels.hookah;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Database {

    private static volatile Database INSTANCE;

    private static final String FILE_NAME = "database.yml";
    // Автосохранение каждую минуту (1200 тиков)
    private static final long SAVE_INTERVAL_TICKS = 1200L;

    private final File file;
    private final FileConfiguration data;

    private Database() {
        file = new File(Controller.plugin.getDataFolder(), FILE_NAME);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            Controller.plugin.saveResource(FILE_NAME, false);
        }
        data = YamlConfiguration.loadConfiguration(file);
        scheduleSaveTask();
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public void increasePuffs(Player player) {
        String name = player.getName();
        int puffs = getPuffs(name);
        data.set(path(name, "count"), puffs + 1);
        data.set(path(name, "ts"), System.currentTimeMillis() / 1000L);
    }

    public int getPuffs(Player player) {
        return getPuffs(player.getName());
    }

    public int getPuffs(String name) {
        return data.getInt(path(name, "count"), 0);
    }

    public long getTimestamp(Player player) {
        return getTimestamp(player.getName());
    }

    public long getTimestamp(String name) {
        return data.getLong(path(name, "ts"), 0L);
    }

    public void deleteUser(Player player) {
        deleteUser(player.getName());
    }

    public void deleteUser(String name) {
        data.set("users." + name, null);
    }

    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            Controller.plugin.getLogger().log(Level.SEVERE, "Не удалось сохранить database.yml: " + file, e);
        }
    }

    /** Удаляет записи с истёкшим временем курения. */
    public void clearGarbage() {
        ConfigurationSection users = data.getConfigurationSection("users");
        if (users == null) return;
        long now = System.currentTimeMillis() / 1000L;
        int duration = Settings.getInstance().smokeDuration;
        for (String name : users.getKeys(false)) {
            if (getPuffs(name) == 0 || now - getTimestamp(name) >= duration) {
                deleteUser(name);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private static String path(String name, String field) {
        return "users." + name + "." + field;
    }

    private void scheduleSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                clearGarbage();
                save();
            }
        }.runTaskTimer(Controller.plugin, SAVE_INTERVAL_TICKS, SAVE_INTERVAL_TICKS);
    }

    public static Database getInstance() {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) INSTANCE = new Database();
            }
        }
        return INSTANCE;
    }
}
