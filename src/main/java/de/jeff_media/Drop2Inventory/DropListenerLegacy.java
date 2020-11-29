package de.jeff_media.Drop2Inventory;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DropListenerLegacy implements Listener {
    Main plugin;
    public DropListenerLegacy(Main main) {
        this.plugin=main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        //System.out.println("BlockBreakEvent "+event.getBlock().getType().name());



        // TODO: Drop shulker box to inv but keep contents
        if (event.getBlock().getType().name().toLowerCase().endsWith("shulker_box")) {
            plugin.debug("Return: Shulker");
            return;
        }

        if (event.isCancelled()) {
            //System.out.println("Return: cancelled");
            return;
        }

        if(plugin.isWorldDisabled(event.getBlock().getWorld().getName())) {
            return;
        }

        Player player = event.getPlayer();

        if (!player.hasPermission("drop2inventory.use")) {
            //System.out.println("Return: permission");
            return;
        }

        // Fix for /reload
        plugin.registerPlayer(event.getPlayer());

        if (player.getGameMode() == GameMode.CREATIVE) {
            //System.out.println("Return: Creative mode");
            return;
        }

        // disabled block?
        if(!plugin.utils.isBlockEnabled(event.getBlock().getType())) {
            //System.out.println("Return: Block disabled");
            return;
        }

        if(plugin.enabled(player) && plugin.getConfig().getBoolean("collect-block-exp")) {
            int experience = event.getExpToDrop();
            event.getPlayer().giveExp(experience);
            event.setExpToDrop(0);
        }

        if(!plugin.getConfig().getBoolean("collect-block-drops")) {
            //System.out.println("Return: no collect-block-drops");
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
        } else {
            if (!setting.hasSeenMessage) {
                setting.hasSeenMessage = true;
                if (plugin.getConfig().getBoolean("show-message-when-breaking-block-and-collection-is-enabled")) {
                    player.sendMessage(plugin.messages.MSG_COMMANDMESSAGE2);
                }
            }
        }



        plugin.dropHandler.drop2inventory(event);
    }

}
