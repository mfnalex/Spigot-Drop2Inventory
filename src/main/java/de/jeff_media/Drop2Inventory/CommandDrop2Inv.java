package de.jeff_media.Drop2Inventory;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandDrop2Inv implements CommandExecutor {
	
	Main plugin;
	
	public CommandDrop2Inv(Main plugin) {
		this.plugin=plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		
		if(!command.getName().equalsIgnoreCase("drop2inventory")) {
			return false;
		}

		// Debug
		if(args.length==3) {
			int x = Integer.parseInt(args[0]);
			int y = Integer.parseInt(args[1]);
			int z = Integer.parseInt(args[2]);
			Player p = (Player) sender;
			p.getWorld().dropItem(new Location(p.getWorld(),x,y,z),new ItemStack(Material.EMERALD));
			p.getWorld().dropItemNaturally(new Location(p.getWorld(),x,y,z),new ItemStack(Material.EMERALD_BLOCK));
			return true;
		}
		// Debug end

		if(plugin.getConfig().getBoolean("always-enabled")) {
			sender.sendMessage(ChatColor.RED+"Drop2Inventory cannot be disabled.");
			return true;
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