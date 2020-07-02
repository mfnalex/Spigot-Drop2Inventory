package de.jeff_media.Drop2Inventory;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

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
}
