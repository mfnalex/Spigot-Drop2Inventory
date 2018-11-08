package de.jeffclan.Drop2Inventory;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Drop2InventoryPlugin extends JavaPlugin implements Listener {
	
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		
		// All you have to do is adding this line in your onEnable method:
        Metrics metrics = new Metrics(this);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		if(event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if(player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		for ( ItemStack item : block.getDrops(player.getInventory().getItemInMainHand())) {
			HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(item);
			for(ItemStack leftoverItem : leftOver.values()) {
				player.getWorld().dropItem(player.getLocation(),leftoverItem);
			}
		}
		
		event.setCancelled(true);
		block.setType(Material.AIR);
	}

}
