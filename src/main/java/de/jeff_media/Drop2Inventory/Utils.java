package de.jeff_media.Drop2Inventory;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.File;

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

	// Returns 16 for 1.16, etc.
	static int getMcVersion(String bukkitVersionString) {
		Pattern p = Pattern.compile("^1\\.(\\d*)\\.");
		Matcher m = p.matcher((bukkitVersionString));
		int version = -1;
		while(m.find()) {
			if(NumberUtils.isNumber(m.group(1)))
			version = Integer.parseInt(m.group(1));
		}
		return version;
	}

	static void renameFileInPluginDir(Main plugin,String oldName, String newName) {
		File oldFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + oldName);
		File newFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + newName);
		oldFile.getAbsoluteFile().renameTo(newFile.getAbsoluteFile());
	}
}
