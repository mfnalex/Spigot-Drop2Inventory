package de.jeff_media.Drop2Inventory;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Predicate;

public class ItemSpawnListener implements @NotNull Listener {

    Main main;
    ArrayList<UUID> drops;

    ItemSpawnListener(Main main) {
        this.main=main;
        drops = new ArrayList<UUID>();
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent e) {
        drops.add(e.getItemDrop().getUniqueId());
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onItemSpawn(ItemSpawnEvent e) {

        main.debug("onItemSpawn");

        if(drops.contains(e.getEntity().getUniqueId())) {
            drops.remove(e.getEntity().getUniqueId());
            return;
        }

        if(!main.getConfig().getBoolean("detect-legacy-drops")) return;
        main.debug("onItemSpawn 1");
        if(e.getEntity() == null) return;
        main.debug("onItemSpawn 2");
        if(e.getEntity().getItemStack()==null) return;
        main.debug("onItemSpawn 3");
        ItemStack is = e.getEntity().getItemStack();

        if(is.getType() == Material.AIR) return;
        main.debug("onItemSpawn 4");
        if(is.getAmount() == 0) return;
        main.debug("onItemSpawn 5");

        Player p = getNearestPlayer(e.getLocation());

        if(p==null) return;
        main.debug("Nearest player: "+p.getName());

        if(isInvFull(p)) {
            main.debug("Skipping collection because inv is full");
            return;
        }

        // Fix for /reload
        main.registerPlayer(p);

        if (p.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        main.debug("onItemSpawn 7");

        // disabled block?
        if (!main.utils.isBlockEnabled(is.getType())) {
            return;
        }
        main.debug("onItemSpawn 8");

        if (!main.getConfig().getBoolean("collect-block-drops")) {
            return;
        }
        main.debug("onItemSpawn 9");


        PlayerSetting setting = main.perPlayerSettings.get(p.getUniqueId().toString());

        if (!main.enabled(p)) {
            if (!setting.hasSeenMessage) {
                setting.hasSeenMessage = true;
                if (main.getConfig().getBoolean("show-message-when-breaking-block")) {
                    p.sendMessage(main.messages.MSG_COMMANDMESSAGE);
                }
            }
            main.debug("disabled");
            return;
        }
        if (!setting.hasSeenMessage) {
            setting.hasSeenMessage = true;
            if (main.getConfig().getBoolean("show-message-when-breaking-block-and-collection-is-enabled")) {
                p.sendMessage(main.messages.MSG_COMMANDMESSAGE2);
            }
        }

        main.debug("onItemSpawn 10");

        main.utils.addOrDrop(is,p);
        e.getEntity().remove();
    }

    private boolean isInvFull(Player p) {
        for(ItemStack i : p.getInventory().getStorageContents()) {
            if(i == null || i.getAmount()==0 || i.getType()==Material.AIR) return false;
        }
        return true;
    }

    @Nullable
    private Player getNearestPlayer(Location location) {

        ArrayList<Player> players = new ArrayList<Player>();
        for(Entity e : location.getWorld().getNearbyEntities(location, 6, 6, 6, new Predicate<Entity>() {
            @Override
            public boolean test(Entity entity) {
                return entity instanceof Player;
            }
        })) {
            players.add((Player) e);
        }

        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                if(o1.getLocation().distance(location) > o2.getLocation().distance(location)) {
                    return 1;
                }
                if(o1.getLocation().distance(location) < o2.getLocation().distance(location)) {
                    return -1;
                }
                return 0;
            }
        });

        if(players.size()>0) return players.get(0);
        return null;
    }

}
