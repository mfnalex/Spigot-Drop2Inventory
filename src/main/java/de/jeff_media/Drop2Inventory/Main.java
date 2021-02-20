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

	int currentConfigVersion = 15;

	BlockDropWrapper blockDropWrapper;
	DropHandler dropHandler;
	PluginUpdateChecker updateChecker;
	Messages messages;
	Utils utils;
	MendingUtils mendingUtils;
	IngotCondenser ingotCondenser;
	ItemSpawnListener itemSpawnListener;
	HotbarStuffer hotbarStuffer;

	HashMap<String, PlayerSetting> perPlayerSettings;

	ArrayList<Material> disabledBlocks;
	ArrayList<String> disabledWorlds;

	boolean blocksIsWhitelist = false;

	ArrayList<String> disabledMobs;

	boolean mobsIsWhitelist = false;
	boolean autoCondense = false;



	final int mcVersion = Utils.getMcVersion(Bukkit.getBukkitVersion());
	boolean usingMatchingConfig = true;
	boolean enabledByDefault = false;
	boolean showMessageWhenBreakingBlock = true;
	boolean showMessageWhenBreakingBlockAndCollectionIsDisabled = false;
	boolean showMessageAgainAfterLogout=true;

	private static final int updateCheckInterval = 86400;

	boolean debug = false;

	public void onEnable() {

		createConfig();

		perPlayerSettings = new HashMap<String, PlayerSetting>();
		messages = new Messages(this);
		ingotCondenser = new IngotCondenser(this);
		itemSpawnListener = new ItemSpawnListener(this);
		CommandDrop2Inv commandDrop2Inv = new CommandDrop2Inv(this);
		hotbarStuffer = new HotbarStuffer(this);
		
		enabledByDefault = getConfig().getBoolean("enabled-by-default");
		showMessageWhenBreakingBlock = getConfig().getBoolean("show-message-when-breaking-block");
		showMessageWhenBreakingBlockAndCollectionIsDisabled = getConfig().getBoolean("show-message-when-breaking-block-and-collection-is-disabled");
		showMessageAgainAfterLogout = getConfig().getBoolean("show-message-again-after-logout");
		autoCondense = getConfig().getBoolean("auto-condense");

		this.getServer().getPluginManager().registerEvents(new Listener(this), this);
		this.getServer().getPluginManager().registerEvents(itemSpawnListener,this);
		if(mcVersion>=13) {
			this.getServer().getPluginManager().registerEvents(new DropListener(this),this);
			debug("MC Version is above 1.13, using BlockDropItemEvent");
		} else {
			blockDropWrapper = new BlockDropWrapper(this);
			dropHandler = new DropHandler(this);
			this.getServer().getPluginManager().registerEvents(new DropListenerLegacy(this),this);
			debug("MC Version is 1.12 or below, using BlockBreakEvent");
		}

		utils = new Utils(this);
		mendingUtils = new MendingUtils(this);

		updateChecker = new PluginUpdateChecker(this,"https://api.jeff-media.de/drop2inventory/drop2inventory-latest-version.txt",
				"https://www.spigotmc.org/resources/1-9-1-16-drop2inventory.62214/","https://github.com/JEFF-Media-GbR/Spigot-Drop2Inventory/blob/master/CHANGELOG.md","https://paypal.me/mfnalex");

		Metrics metrics = new Metrics(this,3532);

		if (getConfig().getString("check-for-updates", "true").equalsIgnoreCase("true")) {
			updateChecker.check((long) updateCheckInterval);
		} else if (getConfig().getString("check-for-updates", "true").equalsIgnoreCase("on-startup")) {
			updateChecker.check();
		}


		
		this.getCommand("drop2inventory").setExecutor(commandDrop2Inv);

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			new Placeholders(this).register();
		}


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
		 //System.out.println("Max Durabilty of "+itemInMainHand.getType().name() + ":"
		 //+itemInMainHand.getType().getMaxDurability());
		 //System.out.println("Current damage: "+damageMeta.getDamage());


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

		if(getConfig().getBoolean("debug",false)) {
			debug=true;
		}

		if(getConfig().isSet("enabled-blocks")) {
			blocksIsWhitelist=true;
		}
		if(getConfig().isSet("enabled-mobs")) {
			mobsIsWhitelist=true;
		}

		disabledBlocks = new ArrayList<>();
		disabledMobs = new ArrayList<>();
		disabledWorlds = new ArrayList<>();
		ArrayList<String> disabledBlocksStrings = (ArrayList<String>) (blocksIsWhitelist ? getConfig().getStringList("enabled-blocks") : getConfig().getStringList("disabled-blocks"));
		for(String s : disabledBlocksStrings) {
			Material m = Material.getMaterial(s.toUpperCase());
			if( m == null) {
				getLogger().warning("Unrecognized material "+s);
			} else {
				disabledBlocks.add(m);
				debug("Adding block to blocks " + (blocksIsWhitelist ? "whitelist" : "blacklist")+": "+m.name());
			}
		}
		for(String s : (mobsIsWhitelist ? getConfig().getStringList("enabled-mobs") : getConfig().getStringList("disabled-mobs"))) {
			disabledMobs.add(s.toLowerCase());
			debug("Adding mob to mobs " + (mobsIsWhitelist ? "whitelist" : "blacklist") + ": "+s.toLowerCase());
		}
		for(String s : getConfig().getStringList("disabled-worlds")) {
			disabledWorlds.add(s.toLowerCase());
			debug("Adding world to worlds blacklist: "+s.toLowerCase());
		}

		if (getConfig().getInt("config-version", 0) != currentConfigVersion) {
			showOldConfigWarning();

			ConfigUpdater configUpdater = new ConfigUpdater(this);
			configUpdater.updateConfig();
			configUpdater = null;
			usingMatchingConfig = true;
			reloadConfig();
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
		getConfig().addDefault("always-enabled",false);
		getConfig().addDefault("check-for-updates", "true");
		getConfig().addDefault("show-message-when-breaking-block", true);
		getConfig().addDefault("show-message-when-breaking-block-and-collection-is-enabled", false);
		getConfig().addDefault("show-message-again-after-logout", true);
		getConfig().addDefault("collect-block-drops", true);
		getConfig().addDefault("collect-mob-drops", true);
		getConfig().addDefault("collect-block-exp", true);
		getConfig().addDefault("collect-mob-exp", true);
		getConfig().addDefault("auto-condense",false);
		getConfig().addDefault("detect-legacy-drops",true);
		getConfig().addDefault("avoid-hotbar",false);

	}

	protected boolean isWorldDisabled(String worldName) {
		return disabledWorlds.contains(worldName.toLowerCase());
	}

	private  void showOldConfigWarning() {
		getLogger().warning("=================================================");
		getLogger().warning("You were using an old config file. Drop2Inventory");
		getLogger().warning("has updated the file to the newest version.");
		getLogger().warning("Your changes have been kept.");
		getLogger().warning("=================================================");
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

		if(getConfig().getBoolean("always-enabled")) return true;
		
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

	void debug(String t) {
		if(debug) getLogger().warning("[DEBUG] "+t);
	}
}
