package de.jeffclan.Drop2Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Drop2InventoryPlugin extends JavaPlugin implements Listener {
	
	BlockDropWrapper blockDropWrapper;
	
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		
		blockDropWrapper = new BlockDropWrapper();
		
		// All you have to do is adding this line in your onEnable method:
        Metrics metrics = new Metrics(this);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		
		if(event.getBlock().getType().name().toLowerCase().endsWith("shulker_box")) {
			return;
		}
		
		//System.out.println("Destroyed Block type: "+event.getBlock().getType().name());
		
		if(event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if(!player.hasPermission("drop2inventory.use")) {
			return;
		}
		
		Block block = event.getBlock();
		boolean hasSilkTouch = false;
		
		if(player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
		
		if(itemInMainHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
			hasSilkTouch=true;
			//System.out.println("Player has SILK TOUCH");
		}
		
		ArrayList<ItemStack> drops;
		
		if(hasSilkTouch) {
			drops = new ArrayList<ItemStack>();
			for(ItemStack item : blockDropWrapper.getSilkTouchDrop(block,itemInMainHand)) {
				drops.add(item);
			}
		} else {
			drops = new ArrayList<ItemStack>();
			for(ItemStack item : blockDropWrapper.getBlockDrop(block, itemInMainHand)) {
				drops.add(item);
			}
		}
		
		for ( ItemStack item : drops) {
			HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(item);
			for(ItemStack leftoverItem : leftOver.values()) {
				player.getWorld().dropItem(player.getLocation(),leftoverItem);
			}
		}
		
		event.setCancelled(true);
		block.setType(Material.AIR);
	}

}
