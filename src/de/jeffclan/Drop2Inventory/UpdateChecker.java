package de.jeffclan.Drop2Inventory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UpdateChecker {

	private Drop2InventoryPlugin plugin;
	String latestVersionLink = String.format("https://api.jeff-media.de/%s/%s-latest-version.txt",
			"drop2inventory","drop2inventory");
	String downloadLink = "https://www.spigotmc.org/resources/1-8-1-13-drop2inventory.62214/";
	private String currentVersion = "undefined";
	private String latestVersion = "undefined";

	public UpdateChecker(Drop2InventoryPlugin plugin) {
		this.plugin = plugin;
	}

	public void sendUpdateMessage(Player p) {
		if (!latestVersion.equals("undefined")) {
			if (!currentVersion.equals(latestVersion)) {
				p.sendMessage(ChatColor.GRAY + "There is a new version of " + ChatColor.GOLD
						+ plugin.getDescription().getName() + ChatColor.GRAY + " available.");
				p.sendMessage(ChatColor.GRAY + "Please download at " + downloadLink);
			}
		}
	}

	public void checkForUpdate() {

		plugin.getLogger().info("Checking for available updates...");

		try {

			HttpURLConnection httpcon = (HttpURLConnection) new URL(latestVersionLink).openConnection();
			httpcon.addRequestProperty("User-Agent",
					plugin.getDescription().getName() + "/" + plugin.getDescription().getVersion());

			BufferedReader reader = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));

			String inputLine = reader.readLine().trim();

			latestVersion = inputLine;
			currentVersion = plugin.getDescription().getVersion().trim();

			if (latestVersion.equals(currentVersion)) {
				plugin.getLogger().info(
						String.format("You are using the latest version of %s.", plugin.getDescription().getName()));
			} else {
				plugin.getLogger().warning("========================================================");
				plugin.getLogger().warning(
						     String.format("There is a new version of %s available!", plugin.getDescription().getName()));
				plugin.getLogger().warning("Latest : " + inputLine);
				plugin.getLogger().warning("Current: " + currentVersion);
				plugin.getLogger().warning("Please update to the newest version. Download:");
				plugin.getLogger().warning(downloadLink);
				plugin.getLogger().warning("========================================================");
			}

			reader.close();
		} catch (Exception e) {
			plugin.getLogger().warning("Could not check for updates.");
		}

	}

}