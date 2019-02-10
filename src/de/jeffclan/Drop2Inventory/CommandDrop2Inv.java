package de.jeffclan.Drop2Inventory;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandDrop2Inv implements CommandExecutor {
	
	Drop2InventoryPlugin plugin;
	
	public CommandDrop2Inv(Drop2InventoryPlugin plugin) {
		this.plugin=plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		
		
		if(!command.getName().equalsIgnoreCase("drop2inventory")) {
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to run this command.");
			return true;
		}
		Player p = (Player) sender;
		if(!sender.hasPermission("drop2inventory.use")) {
			sender.sendMessage(plugin.getCommand("drop2inventory").getPermissionMessage());
			return true;
		}
		
		plugin.togglePlayerSetting(p);
		if(plugin.getPlayerSetting(p).enabled) {
			sender.sendMessage(plugin.messages.MSG_ACTIVATED);
		} else {
			sender.sendMessage(plugin.messages.MSG_DEACTIVATED);
		}
		return true;
		
	}

}