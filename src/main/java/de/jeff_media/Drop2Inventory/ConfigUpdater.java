package de.jeff_media.Drop2Inventory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigUpdater {

    Main plugin;

    ConfigUpdater(Main plugin) {
        this.plugin = plugin;
    }

    // Admins hate config updates. Just relax and let AngelChest update to the newest
    // config version
    // Don't worry! Your changes will be kept

    void updateConfig() {

        try {
            Files.deleteIfExists(new File(plugin.getDataFolder().getAbsolutePath()+File.separator+"config.old.yml").toPath());
        } catch (IOException e) {

        }

        Utils.renameFileInPluginDir(plugin, "config.yml", "config.old.yml");

        plugin.saveDefaultConfig();

        File oldConfigFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.old.yml");
        FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(oldConfigFile);

        try {
            oldConfig.load(oldConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        Map<String, Object> oldValues = oldConfig.getValues(false);

        // Read default config to keep comments
        ArrayList<String> linesInDefaultConfig = new ArrayList<String>();
        try {

            Scanner scanner = new Scanner(
                    new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml"),"UTF-8");
            while (scanner.hasNextLine()) {
                linesInDefaultConfig.add(scanner.nextLine() + "");
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<String> newLines = new ArrayList<String>();
        for (String line : linesInDefaultConfig) {
            String newline = line;
            if (line.startsWith("config-version:")) {

            }
            else if (line.startsWith("disabled-blocks:")) {
                newline = null;
                newLines.add("disabled-blocks:");
                if (plugin.disabledBlocks != null) {
                    for (Material mat : plugin.disabledBlocks) {
                        newLines.add("- " + mat.name());
                    }
                }
            }
            else if (line.startsWith("disabled-mobs:")) {
                newline = null;
                newLines.add("disabled-mobs:");
                if (plugin.disabledMobs != null) {
                    for (String mob : plugin.disabledMobs) {
                        newLines.add("- " + mob);
                    }
                }
            }
            else if (line.startsWith("disabled-worlds:")) {
                newline = null;
                newLines.add("disabled-worlds:");
                if (plugin.disabledWorlds != null) {
                    for (String world : plugin.disabledWorlds) {
                        newLines.add("- " + world);
                    }
                }
            }
            else {
                for (String node : oldValues.keySet()) {
                    if (line.startsWith(node + ":")) {

                        String quotes = "";

                        //if (node.equalsIgnoreCase("sorting-method")) // needs single quotes
                        //	quotes = "'";
                        if (node.startsWith("message-")) // needs double quotes
                            quotes = "\"";



                        newline = node + ": " + quotes + oldValues.get(node).toString() + quotes;

                        break;
                    }
                }
            }
            if (newline != null)
                newLines.add(newline);
        }

        BufferedWriter fw;
        String[] linesArray = newLines.toArray(new String[linesInDefaultConfig.size()]);
        try {
            fw = Files.newBufferedWriter(new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "config.yml").toPath(), StandardCharsets.UTF_8);
            for (int i = 0; i < linesArray.length; i++) {
                fw.write(linesArray[i] + "\n");
            }
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Utils.renameFileInPluginDir(plugin, "config.yml.default", "config.yml");

    }

}