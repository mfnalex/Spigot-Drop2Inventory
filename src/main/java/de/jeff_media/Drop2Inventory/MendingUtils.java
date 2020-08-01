package de.jeff_media.Drop2Inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MendingUtils {
    final Main main;
    MendingUtils(Main main) {
        this.main=main;
    }

    static boolean hasMending(@Nullable ItemStack item,boolean onlyDamaged) {
        if(item==null) return false;
        if(!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if(onlyDamaged) {
            if (!(meta instanceof Damageable)) return false;
            if (((Damageable) meta).getDamage() == 0) return false;
        }
        return meta.hasEnchant(Enchantment.MENDING);
    }

    @Nullable
    ItemStack getReparableItem(PlayerInventory inv,boolean onlyDamaged) {
        // onlyDamaged is true in 1.16+ and only tries to repair damaged items

        List<ItemStack> list = new ArrayList<>();
        if(hasMending(main.utils.getItemInMainHand(inv),onlyDamaged)) list.add(inv.getItemInMainHand());
        if(main.mcVersion>8) {
            if(hasMending(inv.getItemInOffHand(),onlyDamaged)) list.add(inv.getItemInOffHand());
        }
        for(ItemStack item : inv.getArmorContents()) {
            if(hasMending(item,onlyDamaged)) {
                list.add(item);
            }
        }
        if(list.size()==0) return null;
        Collections.shuffle(list);
        return list.get(0);
    }

    static boolean repair(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        Damageable damageable = (Damageable) meta;
        if(damageable.getDamage()==0) return false;
        damageable.setDamage(damageable.getDamage()-2);
        if(damageable.getDamage()<0) {
            damageable.setDamage(0);
        }
        item.setItemMeta((ItemMeta)damageable);
        return true;
    }

    int tryMending(PlayerInventory inv, int exp, boolean onlyDamaged) {
        int repairs = 0;
        while(repairs < exp) {
            ItemStack item = getReparableItem(inv,onlyDamaged);
            if(item==null) break;
            repairs++;
            if(repair(item)) {
                exp--;
            }
        }
        return exp;
    }
}
