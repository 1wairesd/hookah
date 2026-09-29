package org.jedels.hookah.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jedels.hookah.Database;
import org.jedels.hookah.Settings;
import org.jedels.hookah.actions.HookahEffects;
import org.jedels.hookah.actions.HookahMessages;
import org.jedels.hookah.actions.HookahParticles;

public class HookahSmokeEvent {

    private HookahSmokeEvent() {}

    public static void onPlayerSmoked(Player player) {
        Settings settings = Settings.getInstance();
        Database database = Database.getInstance();

        // Reset puff counter if smoke duration has passed
        long now = System.currentTimeMillis() / 1000L;
        if (now - database.getTimestamp(player) >= settings.smokeDuration) {
            database.deleteUser(player);
        }

        database.increasePuffs(player);
        int puffs = database.getPuffs(player);

        GameMode gm = player.getGameMode();
        boolean canBeNegative = !player.hasPermission("hookah.nonegative")
            && (gm == GameMode.SURVIVAL || gm == GameMode.ADVENTURE)
            && puffs > settings.maxSmoked;

        boolean isDied = canBeNegative && settings.damage >= player.getHealth();

        // Warn player on first over-limit puff
        if (canBeNegative && !isDied && puffs == settings.maxSmoked + 1 && !settings.limitMessage.isEmpty()) {
            player.sendMessage(settings.limitMessage);
        }

        HookahParticles.spawnParticles(player);

        String message;
        if (canBeNegative) {
            HookahEffects.addNegativeEffects(player, isDied);
            message = isDied ? settings.getRandomDieMessage() : settings.getRandomDamageMessage();
            if (isDied) database.deleteUser(player);
        } else {
            HookahEffects.addPositiveEffects(player);
            message = settings.getRandomPuffMessage();
        }

        HookahMessages.sendMessageNearby(player, message);
        HookahMessages.playSoundNearby(player);
    }
}
