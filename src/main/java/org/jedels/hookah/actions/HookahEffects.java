package org.jedels.hookah.actions;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jedels.hookah.Settings;

public class HookahEffects {

    private HookahEffects() {}

    public static void addPositiveEffects(Player player) {
        Settings s = Settings.getInstance();
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, s.positiveEffectDuration * 20, 0));
    }

    public static void addNegativeEffects(Player player, boolean isDied) {
        Settings s = Settings.getInstance();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, s.negativeEffectDuration * 20, 0));
        player.damage(s.damage);
        if (isDied) player.damage(1000.0);
    }
}
