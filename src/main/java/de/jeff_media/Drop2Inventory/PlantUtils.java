package de.jeff_media.Drop2Inventory;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

public class PlantUtils {

    static boolean isPlant(Block block) {
        Material[] plants = {Material.CACTUS, Material.SUGAR_CANE, Material.KELP_PLANT, Material.BAMBOO};
        Material mat = block.getType();
        for(Material p : plants) {
            if(mat == p) {
                return true;
            }
        }
        return false;
    }

    static ArrayList<Block> getPlant(Block block) {
        Material mat = block.getType();
        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(block);
        Block next = block.getRelative(BlockFace.UP);
        while(next.getType()==mat) {
            blocks.add(next);
            next = next.getRelative(BlockFace.UP);
        }

        return blocks;
    }

    static void destroyPlant(ArrayList<Block> blocks) {
        blocks.forEach((b) -> b.setType(Material.AIR,false));
    }

    static Material getPlantDrop(Material mat) {
        switch(mat) {
            case KELP_PLANT:
                return Material.KELP;
            default:
                return mat;
        }
    }
}
