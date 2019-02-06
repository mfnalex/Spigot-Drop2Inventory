package de.jeffclan.Drop2Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
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
		
		if(event.getPlayer().getInventory().getItemInMainHand() != null) {
			tryToTakeDurability(event.getPlayer().getInventory().getItemInMainHand(),event.getPlayer());
		}
		
		event.setCancelled(true);
		block.setType(Material.AIR);
	}

	private void tryToTakeDurability(ItemStack itemInMainHand,Player player) {
		if(!(itemInMainHand.getItemMeta() instanceof Damageable) || itemInMainHand.getItemMeta() == null || itemInMainHand==null) {
			//System.out.println("This item has no durability.");
			return;
		}
		Damageable damageMeta = (Damageable) itemInMainHand.getItemMeta();
		//System.out.println("Max Durabilty of "+itemInMainHand.getType().name() + ": "+itemInMainHand.getType().getMaxDurability());
		//System.out.println("Current damage: "+damageMeta.getDamage());
		
		int currentDamage = damageMeta.getDamage();
		short maxDamage = itemInMainHand.getType().getMaxDurability();
		
		int newDamage = currentDamage+1;
		
		damageMeta.setDamage(newDamage);
		itemInMainHand.setItemMeta((ItemMeta)damageMeta);
		
		if(maxDamage > 0 && newDamage >= maxDamage) {
			//System.out.println("This item should break NOW");
			itemInMainHand.setAmount(0);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
		}
	}

}
