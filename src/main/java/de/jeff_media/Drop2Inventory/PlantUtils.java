package de.jeff_media.Drop2Inventory;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.LinkedList;

public class PlantUtils {

    final static String[] plantNames = {
            "CACTUS",
            "SUGAR_CANE",
            "KELP_PLANT",
            "BAMBOO"
    };

    final LinkedList<Material> plants;

    final static BlockFace[] chorusBlockFaces = {
            BlockFace.UP,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
    };

    PlantUtils() {
        plants = new LinkedList<Material>();
        for(String s : plantNames) {
            if(Material.getMaterial(s) != null) {
                plants.add(Material.getMaterial(s));
            }
        }
    }

    static boolean isChorusTree(Block block) {
        if(block.getType().name().equals("CHORUS_PLANT")) return true;
        return false;
    }

    static boolean isPartOfChorusTree(Block block) {
        Material mat = block.getType();
        if(mat.name().equals("CHORUS_PLANT")
                || mat.name().equals("CHORUS_FLOWER")) return true;
        return false;
    }

    boolean isPlant(Block block) {

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
        if(origin.name().equals("KELP_PLANT")
                && current.name().equals("KELP")) return true;
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
        switch(mat.name()) {
            case "KELP_PLANT":
                return Material.getMaterial("KELP");
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
