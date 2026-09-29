package org.jedels.hookah.actions;

import java.text.MessageFormat;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jedels.hookah.Controller;
import org.jedels.hookah.Settings;

public class HookahMessages {

    private HookahMessages() {}

    public static void sendMessageNearby(Player player, String text) {
        Location loc = player.getLocation();
        double radius = Settings.getInstance().messageRadius;
        String message = MessageFormat.format(text, player.getDisplayName());
        for (Player p : Controller.plugin.getServer().getOnlinePlayers()) {
            if (!p.getWorld().equals(loc.getWorld())) continue;
            if (p.getLocation().distance(loc) < radius) {
                p.sendMessage(message);
            }
        }
    }

    public static void playSoundNearby(Player player) {
        Location loc = player.getLocation();
        double radius = Settings.getInstance().messageRadius;
        for (Player p : Controller.plugin.getServer().getOnlinePlayers()) {
            if (!p.getWorld().equals(loc.getWorld())) continue;
            if (p.getLocation().distance(loc) < radius) {
                p.playSound(loc, Sound.ENTITY_BOAT_PADDLE_WATER, 10.0f, 1.0f);
            }
        }
    }
}
