package de.jeff_media.Drop2Inventory;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IngotCondenser {

    final Main main;
    final HashMap<Material,CondensationMap> condensationMap = new HashMap<>();

    IngotCondenser(Main main) {
        this.main=main;
        try {
            initFromFile();
        } catch (IOException e) {
            main.getLogger().warning("Could not read condensation map from file");
        }
    }

    void initFromFile() throws IOException {
        InputStream in = main.getResource("condense.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while(reader.ready()) {
            String line = reader.readLine();
            String parts[] = line.split(",");
            Material item = Material.getMaterial(parts[0].toUpperCase());
            Material block = Material.getMaterial(parts[2].toUpperCase());
            int number = Integer.valueOf(parts[1]);
            if(item==null) {
                main.getLogger().info("Skipping unknown material "+parts[0]);
                continue;
            }
            if(block==null) {
                main.getLogger().info("Skipping unknown material "+parts[2]);
                continue;
            }
            condensationMap.put(item,new CondensationMap(item,number,block));
        }
    }

    void condense(Inventory inv, Material mat) {

            CondensationMap map = condensationMap.get(mat);

            int amount = 0;
            for(ItemStack is : inv.all(map.item).values()) {
                amount += is.getAmount();
            }
            if(amount < map.number) return;
            inv.remove(map.item);
            int blocks = amount / map.number;
            int items = amount % map.number;
            inv.addItem(new ItemStack(map.block,blocks));
            inv.addItem(new ItemStack(map.item,items));
    }

    void condense(Inventory inv) {
        for (Material mat : condensationMap.keySet()) {
            condense(inv,mat);
        }
    }

    class CondensationMap {
        Material item;
        int number;
        Material block;
        CondensationMap(Material mat, int number, Material block) {
            this.item = mat;
            this.number = number;
            this.block = block;
        }
    }


}
