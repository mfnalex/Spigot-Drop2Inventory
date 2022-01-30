package de.jeff_media.Drop2Inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Drop2InventoryEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final List<ItemStack> drops;

    private boolean cancelled;

    public Drop2InventoryEvent(Player player, List<ItemStack> drops) {
        super(player);
        this.drops = drops;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }
}
