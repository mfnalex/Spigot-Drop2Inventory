package de.jeffclan.Drop2Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
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
	UpdateChecker updateChecker;
	
	int currentConfigVersion = 1;
	boolean usingMatchingConfig = true;
	
	private static final int updateCheckInterval = 86400;
	
	public void onEnable() {
		
		createConfig();
		
		this.getServer().getPluginManager().registerEvents(this, this);
		
		blockDropWrapper = new BlockDropWrapper();
		updateChecker = new UpdateChecker(this);
		
		// All you have to do is adding this line in your onEnable method:
        Metrics metrics = new Metrics(this);
        
        if (getConfig().getString("check-for-updates", "true").equalsIgnoreCase("true")) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				public void run() {
					updateChecker.checkForUpdate();
				}
			}, 0L, updateCheckInterval * 20);
		} else if (getConfig().getString("check-for-updates", "true").equalsIgnoreCase("on-startup")) {
			updateChecker.checkForUpdate();
		}
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
		
		// Experience
		int experience = event.getExpToDrop();
		event.getPlayer().setExp(event.getPlayer().getExp() + experience);
		
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
	
	public void createConfig() {
		saveDefaultConfig();
		
		getConfig().addDefault("check-for-updates", "true");
		
		
		if (getConfig().getInt("config-version", 0) != currentConfigVersion) {
			getLogger().warning("========================================================");
			getLogger().warning("YOU ARE USING AN OLD CONFIG FILE!");
			getLogger().warning("This is not a problem, as Drop2Inventory will just use the");
			getLogger().warning("default settings for unset values. However, if you want");
			getLogger().warning("to configure the new options, please go to");
			getLogger().warning("https://www.spigotmc.org/resources/1-8-1-13-drop2inventory.62214/");
			getLogger().warning("and replace your config.yml with the new one. You can");
			getLogger().warning("then insert your old changes into the new file.");
			getLogger().warning("========================================================");
			usingMatchingConfig = false;
		}
	}

}
