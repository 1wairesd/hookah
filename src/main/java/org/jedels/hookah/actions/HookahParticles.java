package org.jedels.hookah.actions;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jedels.hookah.Controller;
import org.jedels.hookah.Settings;

public class HookahParticles {

    private HookahParticles() {}

    public static void spawnParticles(Player player) {
        Location loc = player.getLocation().clone();
        loc.add(0.0, player.isSneaking() ? 1.4 : 1.8, 0.0);
        double radius = Settings.getInstance().messageRadius;
        for (Player p : Controller.plugin.getServer().getOnlinePlayers()) {
            if (!p.getWorld().equals(loc.getWorld())) continue;
            if (p.getLocation().distance(loc) < radius) {
                p.spawnParticle(Particle.CLOUD,       loc, 10, 0.0, 0.0, 0.0, 0.075);
                p.spawnParticle(Particle.WHITE_SMOKE, loc, 25, 0.0, 0.0, 0.0, 0.075);
            }
        }
    }
}
