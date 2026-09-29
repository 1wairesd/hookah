package org.jedels.hookah;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    private static volatile Settings INSTANCE;

    public final int maxSmoked;
    public final int smokeDuration;
    public final int damage;
    public final int positiveEffectDuration;
    public final int negativeEffectDuration;
    public final int messageRadius;
    public final String limitMessage;
    public final String placeMessage;
    public final List<String> puffMessages;
    public final List<String> damageMessages;
    public final List<String> dieMessages;
    public final Set<String> disabledWorlds;

    private Settings() {
        Controller.plugin.saveDefaultConfig();
        FileConfiguration config = Controller.plugin.getConfig();
        maxSmoked              = config.getInt("max-smoked", 5);
        smokeDuration          = config.getInt("smoke-duration", 300);
        damage                 = config.getInt("damage", 1);
        positiveEffectDuration = config.getInt("positive-effect-duration", 5);
        negativeEffectDuration = config.getInt("negative-effect-duration", 10);
        messageRadius          = config.getInt("message-radius", 24);
        limitMessage           = config.getString("limit-message", "");
        placeMessage           = config.getString("place-message", "§f[§eКальян§f] §aКальян установлен! ПКМ чтобы покурить.");
        puffMessages           = config.getStringList("puff-messages");
        damageMessages         = config.getStringList("damage-messages");
        dieMessages            = config.getStringList("die-messages");
        disabledWorlds         = new HashSet<>(config.getStringList("disabled-worlds"));
    }

    public String getRandomPuffMessage()   { return random(puffMessages); }
    public String getRandomDamageMessage() { return random(damageMessages); }
    public String getRandomDieMessage()    { return random(dieMessages); }

    private String random(List<String> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public static Settings getInstance() {
        if (INSTANCE == null) {
            synchronized (Settings.class) {
                if (INSTANCE == null) INSTANCE = new Settings();
            }
        }
        return INSTANCE;
    }
}
