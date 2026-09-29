package org.jedels.hookah.events;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jedels.hookah.Controller;
import org.jedels.hookah.Settings;
import org.jedels.hookah.assets.HookahEventMethod;

public class HookahEvent implements Listener {

    private final List<HookahEventMethod> handlers = new ArrayList<>();

    public HookahEvent() {
        registerHandler(HookahSmokeEvent::onPlayerSmoked);
    }

    public void registerHandler(HookahEventMethod handler) {
        handlers.add(handler);
    }

    /** Кальян — зельеварка на стекле ИЛИ на шерсти любого цвета. */
    private static boolean isHookah(Block block) {
        if (block == null || block.getType() != Material.BREWING_STAND) return false;
        String below = block.getRelative(BlockFace.DOWN).getType().toString();
        return below.endsWith("GLASS") || below.endsWith("WOOL");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return; // игнорируем OFF_HAND дубликат
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!isHookah(event.getClickedBlock())) return;

        Player player = event.getPlayer();
        if (Settings.getInstance().disabledWorlds.contains(player.getWorld().getName())) return;

        event.setCancelled(true);
        for (HookahEventMethod handler : handlers) {
            handler.execute(player);
        }
    }

    /** Уведомляем игрока когда он ставит зельеварку на шерсть — кальян готов. */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block placed = event.getBlockPlaced();
        if (placed.getType() != Material.BREWING_STAND) return;
        String below = placed.getRelative(BlockFace.DOWN).getType().toString();
        if (!below.endsWith("WOOL")) return;
        if (Settings.getInstance().disabledWorlds.contains(event.getPlayer().getWorld().getName())) return;
        event.getPlayer().sendMessage(Settings.getInstance().placeMessage);
    }
}
