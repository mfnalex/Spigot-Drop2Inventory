package de.jeff_media.Drop2Inventory;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class LegacyUtils {

    // For retarded 1.8 versions
    static void tryToTakeDurability18(ItemStack itemInMainHand, Player player) {
        if(itemInMainHand==null || itemInMainHand.getType()== Material.AIR) {
            //System.out.println("null or AIR");
            return;
        }
        String tool = itemInMainHand.getType().name().toLowerCase();
        if(!(tool.endsWith("_PICKAXE") || tool.endsWith("_AXE") || tool.endsWith("_SWORD") || tool.endsWith("_SHOVEL"))) return;

        if (itemInMainHand.getType().isBlock()) {
            //System.out.println("is block");
            return;
        }
        //Damageable damageMeta = (Damageable) itemInMainHand.getItemMeta();
        // System.out.println("Max Durabilty of "+itemInMainHand.getType().name() + ":
        // "+itemInMainHand.getType().getMaxDurability());
        // System.out.println("Current damage: "+damageMeta.getDamage());


        short currentDamage = itemInMainHand.getDurability();
        short maxDamage = itemInMainHand.getType().getMaxDurability();

        short newDamage = (short) (currentDamage + 1);

        itemInMainHand.setDurability(newDamage);

        /*if (maxDamage > 0 && newDamage >= maxDamage) {
            // System.out.println("This item should break NOW");
            itemInMainHand.setAmount(0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f,
                    1.0f);
        }*/
    }
}
