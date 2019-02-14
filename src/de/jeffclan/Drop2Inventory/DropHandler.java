package de.jeffclan.Drop2Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class DropHandler {
	
	Drop2InventoryPlugin plugin;
	Random rand = new Random();
	
	DropHandler(Drop2InventoryPlugin plugin) {
		this.plugin=plugin;
	}
	
	
	
	void drop2inventory(BlockBreakEvent event) {
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		boolean hasSilkTouch = false;
		//boolean hasFortune = false;
		int fortuneLevel = 0;
		
		ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

		if (itemInMainHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
			hasSilkTouch = true;
		}
		
		if(itemInMainHand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
			//hasFortune=true;
			fortuneLevel = itemInMainHand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
		}

		ArrayList<ItemStack> drops;

		if (hasSilkTouch) {
			drops = new ArrayList<ItemStack>();
			for (ItemStack item : plugin.blockDropWrapper.getSilkTouchDrop(block, itemInMainHand)) {
				drops.add(item);
			}
		} else {
			drops = new ArrayList<ItemStack>();
			for (ItemStack item : plugin.blockDropWrapper.getBlockDrop(block, itemInMainHand, fortuneLevel)) {
				drops.add(item);
			}
			// Fortune
			if(fortuneLevel>0) {
				// Maybe only use Fortune if we don't have more than one itemStack?
				for(ItemStack item : drops) {
					item = applyFortune(item,fortuneLevel);
				}
			}
		}

		for (ItemStack item : drops) {
			HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(item);
			for (ItemStack leftoverItem : leftOver.values()) {
				player.getWorld().dropItem(player.getLocation(), leftoverItem);
			}
		}

		if (event.getPlayer().getInventory().getItemInMainHand() != null) {
			plugin.tryToTakeDurability(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer());
		}



		event.setCancelled(true);
		block.setType(Material.AIR);
	}
	
	ItemStack applyFortune(ItemStack itemStack, int fortuneLevel) {
		int r = rand.nextInt(100);
		switch(itemStack.getType()) {
		case COAL:
		case DIAMOND:
		case EMERALD:
		case QUARTZ:
		case LAPIS_LAZULI:
			if(fortuneLevel==1) {
				if(r<33) {
					itemStack.setAmount(itemStack.getAmount()*2);
				}
			} else if(fortuneLevel==2) {
				if(r<25) {
					itemStack.setAmount(itemStack.getAmount()*3);
				} else if(r<50) {
					itemStack.setAmount(itemStack.getAmount()*2);
				}
			} else if(fortuneLevel==3) {
				if(r<20) {
					itemStack.setAmount(itemStack.getAmount()*4);	
				} else if(r<40) {
					itemStack.setAmount(itemStack.getAmount()*3);
				} else if(r<60) {
					itemStack.setAmount(itemStack.getAmount()*2);
				}
			} else if(fortuneLevel>3) {
				int bonus = rand.nextInt(fortuneLevel);
				if(bonus==0) bonus=1;
				itemStack.setAmount(itemStack.getAmount() * bonus);
			}
			break;
		case REDSTONE:
		case CARROT:
		case GLOWSTONE_DUST:
		case PRISMARINE:
		case MELON_SLICE:
		case MELON_SEEDS:
		case NETHER_WART:
		case POTATO:
		case BEETROOT_SEEDS:
		case TALL_GRASS:
		case WHEAT_SEEDS:
			itemStack.setAmount(itemStack.getAmount()+fortuneLevel);
			break;
		default:
			break;
			
		}
		
		return itemStack;
	}

}
