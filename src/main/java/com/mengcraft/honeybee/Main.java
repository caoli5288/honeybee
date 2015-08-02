package com.mengcraft.honeybee;

import org.bukkit.plugin.java.JavaPlugin;

import com.mengcraft.influxdb.InfluxHandler;

public class Main extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        if (getConfig().getBoolean("collect.enable")) {
            InfluxHandler db = new InfluxHandler(
                    getConfig().getString("dataSource.url"),
                    getConfig().getString("dataSource.userName"),
                    getConfig().getString("dataSource.password"),
                    getConfig().getString("dataSource.database"));
            // Try create database if not exists.
            db.createDatabase();
            db.setAsynchronous(true);
            //
            new Collector(this, db).register();
        }
    }
    
}
