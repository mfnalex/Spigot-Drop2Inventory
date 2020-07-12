package de.jeff_media.Drop2Inventory;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Utils {

	Main plugin;

	Utils(Main main) {
		this.plugin=main;
	}
	 boolean isBlockEnabled(Material mat) {
		if (!plugin.blocksIsWhitelist) {
			if (plugin.disabledBlocks.contains(mat.name().toLowerCase()))
				return false;
			return true;
		}
		if (plugin.disabledBlocks.contains(mat.name().toLowerCase()))
			return true;
		return false;
	}

	boolean isMobEnabled(LivingEntity mob) {
		if (!plugin.mobsIsWhitelist) {
			if (plugin.disabledMobs.contains(mob.getType().name().toLowerCase()))
				return false;
			return true;
		}
		if (plugin.disabledMobs.contains(mob.getType().name().toLowerCase()))
			return true;
		return false;
	}

	static void addOrDrop(ItemStack item, Player player) {
		ItemStack[] items = new ItemStack[1];
		items[0] = item;
		addOrDrop(items,player);
	}

	static void addOrDrop(ItemStack[] items, Player player) {
		for(ItemStack item : items) {
			HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
			for (ItemStack leftover : leftovers.values()) {
				player.getWorld().dropItem(player.getLocation(), leftover);
			}
		}
	}
}
