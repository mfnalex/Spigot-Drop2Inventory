package de.jeff_media.Drop2Inventory;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class DropListener implements @NotNull Listener {

    Main plugin;

    DropListener(Main plugin) {
        this.plugin=plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrop(BlockDropItemEvent event) {
        List<Item> items = event.getItems();
        Player player = event.getPlayer();
        World world = event.getPlayer().getLocation().getWorld();

        if (event.isCancelled()) {
            return;
        }

        if (!player.hasPermission("drop2inventory.use")) {
            return;
        }

        if(plugin.getConfig().getBoolean("permissions-per-tool",false) && !(Utils.hasPermissionForThisTool(player.getItemInHand().getType(),player))) {
            return;
        }

        // Fix for /reload
        plugin.registerPlayer(event.getPlayer());

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // disabled block?
        if (!plugin.utils.isBlockEnabled(event.getBlockState().getType())) {
            return;
        }

        if (!plugin.getConfig().getBoolean("collect-block-drops")) {
            return;
        }


        PlayerSetting setting = plugin.perPlayerSettings.get(player.getUniqueId().toString());

        if (!plugin.enabled(player)) {
            if (!setting.hasSeenMessage) {
                setting.hasSeenMessage = true;
                if (plugin.getConfig().getBoolean("show-message-when-breaking-block")) {
                    player.sendMessage(plugin.messages.MSG_COMMANDMESSAGE);
                }
            }
            return;
        }
        if (!setting.hasSeenMessage) {
            setting.hasSeenMessage = true;
            if (plugin.getConfig().getBoolean("show-message-when-breaking-block-and-collection-is-enabled")) {
                player.sendMessage(plugin.messages.MSG_COMMANDMESSAGE2);
            }
        }

        /*for (Item item : items) {
            HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(item.getItemStack());
            for (ItemStack leftover : leftovers.values()) {
                world.dropItem(player.getLocation(), leftover);
            }
        }*/
        for(Item item : items) {
            plugin.utils.addOrDrop(item.getItemStack(),event.getPlayer());
        }

        event.setCancelled(true);
    }
}
