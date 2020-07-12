package de.jeff_media.Drop2Inventory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.jeff_media.PluginUpdateChecker.PluginUpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;


public class Main extends JavaPlugin {

	//BlockDropWrapper blockDropWrapper;
	//DropHandler dropHandler;
	PluginUpdateChecker updateChecker;
	Messages messages;
	Utils utils;

	HashMap<String, PlayerSetting> perPlayerSettings;

	ArrayList<Material> disabledBlocks;

	boolean blocksIsWhitelist = false;

	ArrayList<String> disabledMobs;

	boolean mobsIsWhitelist = false;

	int currentConfigVersion = 8;
	boolean usingMatchingConfig = true;
	boolean enabledByDefault = false;
	boolean showMessageWhenBreakingBlock = true;
	boolean showMessageWhenBreakingBlockAndCollectionIsDisabled = false;
	boolean showMessageAgainAfterLogout=true;

	private static final int updateCheckInterval = 86400;

	public void onEnable() {

		createConfig();

		perPlayerSettings = new HashMap<String, PlayerSetting>();
		//dropHandler = new DropHandler(this);
		messages = new Messages(this);
		CommandDrop2Inv commandDrop2Inv = new CommandDrop2Inv(this);
		
		enabledByDefault = getConfig().getBoolean("enabled-by-default");
		showMessageWhenBreakingBlock = getConfig().getBoolean("show-message-when-breaking-block");
		showMessageWhenBreakingBlockAndCollectionIsDisabled = getConfig().getBoolean("show-message-when-breaking-block-and-collection-is-disabled");
		showMessageAgainAfterLogout = getConfig().getBoolean("show-message-again-after-logout");

		this.getServer().getPluginManager().registerEvents(new Listener(this), this);

		utils = new Utils(this);

		//blockDropWrapper = new BlockDropWrapper();
		updateChecker = new PluginUpdateChecker(this,"https://api.jeff-media.de/drop2inventory/drop2inventory-latest-version.txt",
				"https://www.spigotmc.org/resources/1-9-1-16-drop2inventory.62214/","https://github.com/JEFF-Media-GbR/Spigot-Drop2Inventory/blob/master/CHANGELOG.md","https://paypal.me/mfnalex");

		Metrics metrics = new Metrics(this,3532);

		if (getConfig().getString("check-for-updates", "true").equalsIgnoreCase("true")) {
			updateChecker.check((long) updateCheckInterval);
		} else if (getConfig().getString("check-for-updates", "true").equalsIgnoreCase("on-startup")) {
			updateChecker.check();
		}
		
		this.getCommand("drop2inventory").setExecutor(commandDrop2Inv);
	}
	
	@Override
	public void onDisable() {
		for (Player p : getServer().getOnlinePlayers()) {
			unregisterPlayer(p);
		}
}

	

	void tryToTakeDurability(ItemStack itemInMainHand, Player player) {
		if(itemInMainHand==null || itemInMainHand.getType()==Material.AIR) {
			//System.out.println("null or AIR");
			return;
		}
		if(!(itemInMainHand instanceof Damageable)) {
			//System.out.println("not instanceof Damageable");
			return;
		}
		if(itemInMainHand.getItemMeta()==null) {
			//System.out.println("itemMeta null");
			return;
		}
		if (itemInMainHand.getType().isBlock()) {
			//System.out.println("is block");
			return;
		}
		Damageable damageMeta = (Damageable) itemInMainHand.getItemMeta();
		// System.out.println("Max Durabilty of "+itemInMainHand.getType().name() + ":
		// "+itemInMainHand.getType().getMaxDurability());
		// System.out.println("Current damage: "+damageMeta.getDamage());


		int currentDamage = damageMeta.getDamage();
		short maxDamage = itemInMainHand.getType().getMaxDurability();

		int newDamage = currentDamage + 1;

		damageMeta.setDamage(newDamage);
		itemInMainHand.setItemMeta((ItemMeta) damageMeta);

		if (maxDamage > 0 && newDamage >= maxDamage) {
			// System.out.println("This item should break NOW");
			itemInMainHand.setAmount(0);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f,
					1.0f);
		}
	}

	public void createConfig() {
		saveDefaultConfig();

		if (getConfig().getInt("config-version", 0) < 4) {
			getLogger().warning("========================================================");
			getLogger().warning("You are using a config file that has been generated");
			getLogger().warning("prior to Drop2Inventory version 3.3.");
			getLogger().warning("To allow everyone to use the new features, your config");
			getLogger().warning("has been renamed to config.old.yml and a new one has");
			getLogger().warning("been generated. Please examine the new config file to");
			getLogger().warning("see the new possibilities and adjust your settings.");
			getLogger().warning("========================================================");

			File configFile = new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml");
			File oldConfigFile = new File(getDataFolder().getAbsolutePath() + File.separator + "config.old.yml");
			if (oldConfigFile.getAbsoluteFile().exists()) {
				oldConfigFile.getAbsoluteFile().delete();
			}
			configFile.getAbsoluteFile().renameTo(oldConfigFile.getAbsoluteFile());
			saveDefaultConfig();
			try {
				getConfig().load(configFile.getAbsoluteFile());
			} catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
				getLogger().warning("Could not load freshly generated config file!");
				e.printStackTrace();
			}
		}
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

		File playerDataFolder = new File(getDataFolder().getPath() + File.separator + "playerdata");
		if (!playerDataFolder.getAbsoluteFile().exists()) {
			playerDataFolder.mkdir();
		}
		
		// Default settings
		getConfig().addDefault("enabled-by-default", false);
		getConfig().addDefault("check-for-updates", "true");
		getConfig().addDefault("show-message-when-breaking-block", true);
		getConfig().addDefault("show-message-when-breaking-block-and-collection-is-enabled", false);
		getConfig().addDefault("show-message-again-after-logout", true);
		getConfig().addDefault("collect-block-drops", true);
		getConfig().addDefault("collect-mob-drops", true);
		getConfig().addDefault("collect-block-exp", true);
		getConfig().addDefault("collect-mob-exp", true);
		disabledBlocks = new ArrayList<>();
		disabledMobs = new ArrayList<>();
		ArrayList<String> disabledBlocksStrings = (ArrayList<String>) getConfig().getStringList("disabled-blocks");
		for(String s : disabledBlocksStrings) {
			Material m = Material.getMaterial(s);
			if( m == null) {
				getLogger().warning("Unrecognized material "+s);
			} else {
				disabledBlocks.add(m);
			}
		}
		for(String s : getConfig().getStringList("disabled-mobs")) {
				disabledMobs.add(s.toLowerCase());
		}
	}
	
	public PlayerSetting getPlayerSetting(Player p) {
		registerPlayer(p);
		return perPlayerSettings.get(p.getUniqueId().toString());
}
	
	public void registerPlayer(Player p) {
		if (!perPlayerSettings.containsKey(p.getUniqueId().toString())) {
			
			File playerFile = new File(getDataFolder() + File.separator + "playerdata",
					p.getUniqueId().toString() + ".yml");
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);

			boolean activeForThisPlayer;

			if (!playerFile.exists()) {
				activeForThisPlayer = enabledByDefault;
			} else {
				activeForThisPlayer = playerConfig.getBoolean("enabled");
			}

			PlayerSetting newSettings = new PlayerSetting(activeForThisPlayer);
			if (!getConfig().getBoolean("show-message-again-after-logout")) {
				newSettings.hasSeenMessage = playerConfig.getBoolean("hasSeenMessage");
			}
			
			
			perPlayerSettings.put(p.getUniqueId().toString(), newSettings);
		}
}
	
	public void togglePlayerSetting(Player p) {
		registerPlayer(p);
		boolean enabled = perPlayerSettings.get(p.getUniqueId().toString()).enabled;
		perPlayerSettings.get(p.getUniqueId().toString()).enabled = !enabled;
}
	
	boolean enabled(Player p) {
		
		// The following is for all the lazy server admins who use /reload instead of properly restarting their
		// server ;) I am sometimes getting stacktraces although it is clearly stated that /reload is NOT
		// supported. So, here is a quick fix
		if(perPlayerSettings == null) {
			perPlayerSettings = new HashMap<String, PlayerSetting>();
		}
		registerPlayer(p);
		// End of quick fix
		
		return perPlayerSettings.get(p.getUniqueId().toString()).enabled;
}
	
	void unregisterPlayer(Player p) {
		UUID uniqueId = p.getUniqueId();
		if (perPlayerSettings.containsKey(uniqueId.toString())) {
			PlayerSetting setting = perPlayerSettings.get(p.getUniqueId().toString());
			File playerFile = new File(getDataFolder() + File.separator + "playerdata",
					p.getUniqueId().toString() + ".yml");
			YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
			playerConfig.set("enabled", setting.enabled);
			playerConfig.set("hasSeenMessage", setting.hasSeenMessage);
			try {
				playerConfig.save(playerFile);
			} catch (IOException e) {
				e.printStackTrace();
			}

			perPlayerSettings.remove(uniqueId.toString());
		}
}

}
