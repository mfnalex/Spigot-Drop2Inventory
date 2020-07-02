package de.jeff_media.Drop2Inventory;

import org.bukkit.ChatColor;

public class Messages {

	Main plugin;

	final String MSG_ACTIVATED, MSG_DEACTIVATED, MSG_COMMANDMESSAGE, MSG_COMMANDMESSAGE2;

	Messages(Main plugin) {
		this.plugin = plugin;

		MSG_ACTIVATED = ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
				.getString("message-enabled", "&7Automatic drop collection has been &aenabled&7.&r"));

		MSG_DEACTIVATED = ChatColor.translateAlternateColorCodes('&', plugin.getConfig()
				.getString("message-disabled", "&7Automatic drop collection has been &cdisabled&7.&r"));

		MSG_COMMANDMESSAGE = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(
				"message-when-breaking-block", "&7Hint: Type &6/drop2inventory&7 or &6/drop2inv&7 to enable automatic drop collection."));

		MSG_COMMANDMESSAGE2 = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(
"message-when-breaking-block2", "&7Hint: Type &6/drop2inventory&7 or &6/drop2inv&7 to disable automatic drop collection."));
	}

}