package de.jeffclan.Drop2Inventory;

import org.bukkit.Material;

public class Utils {
	public static boolean isBlockEnabled(Material mat, Drop2InventoryPlugin plugin) {
		
		if(plugin.disabledBlocks.contains(mat.name().toLowerCase())) {
		//if(plugin.disabledWorlds.stream().noneMatch(s -> s.equalsIgnoreCase(world.getName()))) {
			return false;
		}
		
		return true;
	}
}
