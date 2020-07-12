package de.jeff_media.Drop2Inventory;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class Listener implements org.bukkit.event.Listener {

	Main plugin;

	Listener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		plugin.registerPlayer(event.getPlayer());


	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.unregisterPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.getKiller() == null) {
			return;
		}

		if (!entity.getKiller().hasPermission("drop2inventory.use")) {
			return;
		}

		// Fix for /reload
		plugin.registerPlayer(entity.getKiller());

		if (!plugin.enabled(entity.getKiller())) {
			return;
		}

		if (entity.getKiller().getGameMode() == GameMode.CREATIVE) {
			return;
		}

		if (!plugin.utils.isMobEnabled(entity)) {
			return;
		}


		if (plugin.getConfig().getBoolean("collect-mob-exp")) {
			int exp = event.getDroppedExp();
			event.setDroppedExp(0);
			entity.getKiller().giveExp(exp);
		}
		if (!plugin.getConfig().getBoolean("collect-mob-drops")) {
			return;
		}

		//entity.getKiller().sendMessage("You have killed entity "+entity.getName());

		List<ItemStack> drops = event.getDrops();
		for (ItemStack is : drops) {
			entity.getKiller().getInventory().addItem(is);
		}
		event.getDrops().clear();
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {

		//System.out.println("BlockBreakEvent "+event.getBlock().getType().name());


		// TODO: Drop shulker box to inv but keep contents
		/*if (event.getBlock().getType().name().toLowerCase().endsWith("shulker_box")) {
			return;
		}*/

		if (event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();

		if (!player.hasPermission("drop2inventory.use")) {
			return;
		}

		// Fix for /reload
		plugin.registerPlayer(event.getPlayer());

		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		// disabled block?
		if (!plugin.utils.isBlockEnabled(event.getBlock().getType())) {
			return;
		}

		if (plugin.enabled(player) && plugin.getConfig().getBoolean("collect-block-exp")) {
			int experience = event.getExpToDrop();
			event.getPlayer().giveExp(experience);
			event.setExpToDrop(0);
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
		} else {
			if (!setting.hasSeenMessage) {
				setting.hasSeenMessage = true;
				if (plugin.getConfig().getBoolean("show-message-when-breaking-block-and-collection-is-enabled")) {
					player.sendMessage(plugin.messages.MSG_COMMANDMESSAGE2);
				}
			}
		}


		//plugin.dropHandler.drop2inventory(event);
	}

	@EventHandler
	public void onItemDrop(BlockDropItemEvent event) {
		List<Item> items = event.getItems();
		Player player = event.getPlayer();
		World world = event.getPlayer().getLocation().getWorld();

		// TODO: Drop shulker box to inv but keep contents
		/*if (event.getBlock().getType().name().toLowerCase().endsWith("shulker_box")) {
			return;
		}*/

		if (event.isCancelled()) {
			return;
		}

		if (!player.hasPermission("drop2inventory.use")) {
			return;
		}

		// Fix for /reload
		plugin.registerPlayer(event.getPlayer());

		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		// disabled block?
		if (!plugin.utils.isBlockEnabled(event.getBlock().getType())) {
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
		} else {
			if (!setting.hasSeenMessage) {
				setting.hasSeenMessage = true;
				if (plugin.getConfig().getBoolean("show-message-when-breaking-block-and-collection-is-enabled")) {
					player.sendMessage(plugin.messages.MSG_COMMANDMESSAGE2);
				}
			}

			for (Item item : items) {
				HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(item.getItemStack());
				for (ItemStack leftover : leftovers.values()) {
					world.dropItem(player.getLocation(), leftover);
				}
			}

			event.setCancelled(true);

		}

	}
}
