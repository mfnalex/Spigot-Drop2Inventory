package de.jeff_media.Drop2Inventory;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

public class PlantUtils {

    final static Material[] plants = {
            Material.CACTUS,
            Material.SUGAR_CANE,
            Material.KELP_PLANT,
            Material.BAMBOO
    };

    final static BlockFace[] chorusBlockFaces = {
            BlockFace.UP,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
    };

    static boolean isChorusTree(Block block) {
        if(block.getType() == Material.CHORUS_PLANT) return true;
        return false;
    }

    static boolean isPartOfChorusTree(Block block) {
        Material mat = block.getType();
        if(mat == Material.CHORUS_PLANT
                || mat == Material.CHORUS_FLOWER) return true;
        return false;
    }

    static boolean isPlant(Block block) {

        Material mat = block.getType();
        for(Material p : plants) {
            if(mat == p) {
                return true;
            }
        }
        return false;
    }

    static boolean matchesPlant(Material origin, Material current) {
        if(origin==current) return true;
        if(origin==Material.KELP_PLANT
                && current==Material.KELP) return true;
        return false;
    }

    static ArrayList<Block> getPlant(Block block) {
        Material mat = block.getType();
        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(block);
        Block next = block.getRelative(BlockFace.UP);
        while(matchesPlant(mat,next.getType())) {
            blocks.add(next);
            next = next.getRelative(BlockFace.UP);
        }
        return blocks;
    }

    public static void getChorusTree(Block block, ArrayList<Block> list) {

        Block currentBlock = block;

        if(isPartOfChorusTree(currentBlock) /*&& list.size()<maxTreeSize */) {
            if(!list.contains(currentBlock)) {
                list.add(currentBlock);

                for(BlockFace face:chorusBlockFaces) {
                    if(isPartOfChorusTree(currentBlock.getRelative(face))) {
                        getChorusTree(currentBlock.getRelative(face),list);
                    }
                }

            }

        }
    }

    static void destroyPlant(ArrayList<Block> blocks) {
        blocks.forEach((b) -> b.setType(Material.AIR,true));
    }

    static Material getPlantDrop(Material mat) {
        switch(mat) {
            case KELP_PLANT:
                return Material.KELP;
            default:
                return mat;
        }
    }

    static int getAmountInList(ArrayList<Block> blocks, Material search) {
        int i = 0;
        for(Block block : blocks) {
            if(block.getType()==search) i++;
        }
        return i;
    }
}
