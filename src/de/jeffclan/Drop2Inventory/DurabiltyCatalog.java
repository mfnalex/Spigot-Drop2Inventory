package de.jeffclan.Drop2Inventory;

import org.bukkit.Material;

public class DurabiltyCatalog {

	static int getDurability(Material type) {
		switch (type) {
		case WOODEN_SWORD:
			return 60;
		case STONE_SWORD:
			return 132;
		case IRON_SWORD:
			return 251;
		case DIAMOND_SWORD:
			return 1562;
		case GOLDEN_SWORD:
			return 33;
			
		case WOODEN_HOE:
			return 60;
		case STONE_HOE:
			return 132;
		case IRON_HOE:
			return 251;
		case DIAMOND_HOE:
			return 1562;
		case GOLDEN_HOE:
			return 33;

		case WOODEN_AXE:
			return 60;
		case STONE_AXE:
			return 132;
		case IRON_AXE:
			return 251;
		case DIAMOND_AXE:
			return 1562;
		case GOLDEN_AXE:
			return 33;

		case WOODEN_SHOVEL:
			return 60;
		case STONE_SHOVEL:
			return 132;
		case IRON_SHOVEL:
			return 251;
		case DIAMOND_SHOVEL:
			return 1562;
		case GOLDEN_SHOVEL:
			return 33;

		case WOODEN_PICKAXE:
			return 60;
		case STONE_PICKAXE:
			return 132;
		case IRON_PICKAXE:
			return 251;
		case DIAMOND_PICKAXE:
			return 1562;
		case GOLDEN_PICKAXE:
			return 33;

		default:
			return -1;

		}

	}

}
