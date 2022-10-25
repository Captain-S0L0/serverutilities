package com.terriblefriends.serverutilities.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class Config {
    private static final File file = new File(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().toFile(), "serverutilities.json");
    private static Config INSTANCE = new Config();

    public String discordUrl = "http://discord.terriblefriends.ml";
    public String dynmapUrl = "http://terriblefriends.ml/dynmap/web";

    public static void load() {
        Gson gson = new Gson();

        try (Reader reader = new FileReader(file)) {
            INSTANCE = gson.fromJson(reader, Config.class);
        } catch (Exception e) {
            if (file.exists()) {
                System.out.println("serverutils couldn't load config, deleting it...");
                file.delete();
            } else {
                System.out.println("serverutils couldn't find config");
            }
        }
    }

    public static void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(file)) {

            gson.toJson(INSTANCE, writer);
        } catch (IOException e) {
            System.out.println("serverutils could't save config");
            e.printStackTrace();
        }
    }

    public static Config get() {
        return INSTANCE;
    }
}
