package de.jeff_media.Drop2Inventory;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Listener implements org.bukkit.event.Listener {

    Main plugin;
    Random random = new Random();
    boolean onlyDamaged;
    PlantUtils plantUtils = new PlantUtils();

    Listener(Main plugin) {
        this.plugin = plugin;
        boolean onlyDamaged = plugin.mcVersion >= 16 ? true : false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        plugin.registerPlayer(event.getPlayer());


    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.unregisterPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getKiller() == null) {
            return;
        }

        if(!(entity.getKiller() instanceof Player)) {
            return;
        }

        Player p = (Player) entity.getKiller();

        if (!entity.getKiller().hasPermission("drop2inventory.use")) {
            return;
        }

        // Fix for /reload
        plugin.registerPlayer(entity.getKiller());

        if (!plugin.enabled(entity.getKiller())) {
            return;
        }

        if (entity.getKiller().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (!plugin.utils.isMobEnabled(entity)) {
            return;
        }


        if (plugin.getConfig().getBoolean("collect-mob-exp")) {
            int exp = event.getDroppedExp();

            if(MendingUtils.hasMending(p.getInventory().getItemInMainHand(),false)) {
                exp = MendingUtils.tryMending(p.getInventory(), exp, onlyDamaged);
            }

            event.setDroppedExp(0);
            entity.getKiller().giveExp(exp);
        }
        if (!plugin.getConfig().getBoolean("collect-mob-drops")) {
            return;
        }

        //entity.getKiller().sendMessage("You have killed entity "+entity.getName());

        List<ItemStack> drops = event.getDrops();
        Utils.addOrDrop(drops.toArray(new ItemStack[0]),entity.getKiller());
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.MONITOR) // Monitor because some plugins like BannerBoard are too stupid to listen on LOWEST when they only want to cancel events regarding their own stuff...
    public void onItemFrameRemoveItem(EntityDamageByEntityEvent event) {

        if(!(event.getDamager() instanceof Player)) return;
        Player p = (Player) event.getDamager();
        if(event.isCancelled()) {
            plugin.debug("EntityDamageByEntityEvent is cancelled");
            return;
        }
        plugin.debug("EntityDamageByEntityEvent is NOT cancelled");
        if(!isDrop2InvEnabled(p,plugin.getPlayerSetting(p))) return;
        if(event.getEntity() instanceof ItemFrame) {
            ItemFrame frame = (ItemFrame) event.getEntity();
            ItemStack content = frame.getItem();
            if(content != null && content.getType()!=Material.AIR) {
                plugin.debug("The frame contained "+content.toString());
                Utils.addOrDrop(content,p);
            } else {
                return;
            }
            frame.setItem(null);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR) // Monitor because some plugins like BannerBoard are too stupid to listen on LOWEST when they only want to cancel events regarding their own stuff...
    public void onHangingBreak(HangingBreakByEntityEvent event) {
        if(!(event.getRemover() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getRemover();
        if(event.isCancelled()) return;
        if(!isDrop2InvEnabled(p,plugin.getPlayerSetting(p))) return;
        plugin.debug("Player removed a Hanging");
        if(event.getEntity() instanceof ItemFrame) {
            plugin.debug("It was an Item frame.");
            ItemFrame frame = (ItemFrame) event.getEntity();
            ItemStack content = frame.getItem();
            if(content != null) {
                plugin.debug("The frame contained "+content.toString());
                Utils.addOrDrop(content,p);
            }
            Utils.addOrDrop(new ItemStack(Material.ITEM_FRAME),p);
            event.getEntity().remove();
            event.setCancelled(true);
        }

        if(event.getEntity() instanceof Painting) {
            Utils.addOrDrop(new ItemStack(Material.PAINTING),p);
            event.getEntity().remove();
        }

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        //System.out.println("BlockBreakEvent "+event.getBlock().getType().name());


        // TODO: Drop shulker box to inv but keep contents
		/*if (event.getBlock().getType().name().toLowerCase().endsWith("shulker_box")) {
			return;
		}*/

        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();



        // disabled block?
        if (!plugin.utils.isBlockEnabled(event.getBlock().getType())) {
            return;
        }

        if (plugin.enabled(player) && plugin.getConfig().getBoolean("collect-block-exp")) {
            int experience = event.getExpToDrop();
            if(MendingUtils.hasMending(event.getPlayer().getInventory().getItemInMainHand(),false)) {
                experience = MendingUtils.tryMending(event.getPlayer().getInventory(), experience,onlyDamaged);
            }
            event.getPlayer().giveExp(experience);
            event.setExpToDrop(0);
        }




        PlayerSetting setting = plugin.perPlayerSettings.get(player.getUniqueId().toString());

        if (!isDrop2InvEnabled(player, setting)) return;

        if(plantUtils.isPlant(event.getBlock())) {
            event.setDropItems(false);
            ArrayList<Block> plant = PlantUtils.getPlant(event.getBlock());
            int extraAmount = plant.size();
            ItemStack plantItems = new ItemStack(PlantUtils.getPlantDrop(event.getBlock().getType()), extraAmount);
            Utils.addOrDrop(plantItems,event.getPlayer());
            PlantUtils.destroyPlant(plant);
        } else if(PlantUtils.isChorusTree(event.getBlock())) {
            // Note:
            // Chorus flower only drop themselves when broken directly,
            // but not when they drop because the chorus plant is broken
            ArrayList<Block> chorusTree = new ArrayList<Block>();
            event.setDropItems(false);
             PlantUtils.getChorusTree(event.getBlock(),chorusTree);
            int extraAmountChorusPlant = PlantUtils.getAmountInList(chorusTree,Material.CHORUS_PLANT);
            int extraAmountChorusFruit = 0;

            for(int i = 0; i < extraAmountChorusPlant; i++) {
                if(random.nextInt(100)>=50) {
                    extraAmountChorusFruit++;
                }
            }

            ItemStack flowerDrops = new ItemStack(Material.CHORUS_FRUIT, extraAmountChorusFruit);
            Utils.addOrDrop(flowerDrops,event.getPlayer());
            PlantUtils.destroyPlant(chorusTree);
        } else if(event.getBlock().getState() instanceof Furnace) {

            FurnaceInventory finv = ((Furnace) event.getBlock().getState()).getInventory();

            if(finv.getFuel()!=null) {
                Utils.addOrDrop(finv.getFuel(),event.getPlayer());
                finv.setFuel(null);
            }
            if(finv.getSmelting()!=null) {
                Utils.addOrDrop(finv.getSmelting(),event.getPlayer());
                finv.setSmelting(null);
            }
            if(finv.getResult()!=null) {
                Utils.addOrDrop(finv.getResult(),event.getPlayer());
                finv.setResult(null);
            }


        }



        //plugin.dropHandler.drop2inventory(event);
    }

    private boolean isDrop2InvEnabled(Player player, PlayerSetting setting) {

        if(plugin.getConfig().getBoolean("always-enabled")) return true;

        if (!player.hasPermission("drop2inventory.use")) {
            return false;
        }

        // Fix for /reload
        plugin.registerPlayer(player);

        if (player.getGameMode() == GameMode.CREATIVE) {
            return false;
        }



        if (!plugin.getConfig().getBoolean("collect-block-drops")) {
            return false;
        }
        if (!plugin.enabled(player)) {
            if (!setting.hasSeenMessage) {
                setting.hasSeenMessage = true;
                if (plugin.getConfig().getBoolean("show-message-when-breaking-block")) {
                    player.sendMessage(plugin.messages.MSG_COMMANDMESSAGE);
                }
            }
            return false;
        } else {
            if (!setting.hasSeenMessage) {
                setting.hasSeenMessage = true;
                if (plugin.getConfig().getBoolean("show-message-when-breaking-block-and-collection-is-enabled")) {
                    player.sendMessage(plugin.messages.MSG_COMMANDMESSAGE2);
                }
            }
        }
        return true;
    }

    @EventHandler
    public void onItemDrop(BlockDropItemEvent event) {
        List<Item> items = event.getItems();
        Player player = event.getPlayer();
        World world = event.getPlayer().getLocation().getWorld();

        if (event.isCancelled()) {
            return;
        }

        if (!player.hasPermission("drop2inventory.use")) {
            return;
        }

        // Fix for /reload
        plugin.registerPlayer(event.getPlayer());

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // disabled block?
        if (!plugin.utils.isBlockEnabled(event.getBlock().getType())) {
            return;
        }

        if (!plugin.getConfig().getBoolean("collect-block-drops")) {
            return;
        }


        PlayerSetting setting = plugin.perPlayerSettings.get(player.getUniqueId().toString());

        if (!plugin.enabled(player)) {
            if (!setting.hasSeenMessage) {
                setting.hasSeenMessage = true;
                if (plugin.getConfig().getBoolean("show-message-when-breaking-block")) {
                    player.sendMessage(plugin.messages.MSG_COMMANDMESSAGE);
                }
            }
            return;
        }
        if (!setting.hasSeenMessage) {
            setting.hasSeenMessage = true;
            if (plugin.getConfig().getBoolean("show-message-when-breaking-block-and-collection-is-enabled")) {
                player.sendMessage(plugin.messages.MSG_COMMANDMESSAGE2);
            }
        }

        /*for (Item item : items) {
            HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(item.getItemStack());
            for (ItemStack leftover : leftovers.values()) {
                world.dropItem(player.getLocation(), leftover);
            }
        }*/
        for(Item item : items) {
            Utils.addOrDrop(item.getItemStack(),event.getPlayer());
            if(plugin.autoCondense) {
                plugin.ingotCondenser.condense(event.getPlayer().getInventory(), item.getItemStack().getType());
            }
        }



        event.setCancelled(true);
    }
}
