package de.jeffclan.Drop2Inventory;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Listener implements org.bukkit.event.Listener {
	
	Drop2InventoryPlugin plugin;
	
	Listener(Drop2InventoryPlugin plugin) {
		this.plugin=plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {


		if (event.getPlayer().isOp()) {
			plugin.updateChecker.sendUpdateMessage(event.getPlayer());
		}
		
		plugin.registerPlayer(event.getPlayer());

		
}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.unregisterPlayer(event.getPlayer());
}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {

		if (event.getBlock().getType().name().toLowerCase().endsWith("shulker_box")) {
			return;
		}

		if (event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();

		if (!player.hasPermission("drop2inventory.use")) {
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE) {
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
