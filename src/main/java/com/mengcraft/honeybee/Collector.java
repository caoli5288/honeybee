package com.mengcraft.honeybee;

import java.math.BigDecimal;

import org.bukkit.Server;

import com.mengcraft.influxdb.InfluxHandler;

public class Collector implements Runnable {

    private boolean registered;
    
    private long lastTime;
    private double secend;
    private double tps;
    
    private final Main main;
    private final Server server;
    private final InfluxHandler db;
    private final String tag;

    private final int daily;

    public Collector(Main main, InfluxHandler db) {
        this.lastTime = System.currentTimeMillis();
        this.main = main;
        this.server = main.getServer();
        this.db = db;
        this.tag = main.getConfig().getString("collect.tag");
        this.daily = main.getConfig().getInt("collect.interval");
    }

    public void run() {
        secend = (now() - lastTime) / 1000;
        tps = (daily * 20) / secend;
        // Record.
        db.write("player_value")
          .where("server", tag)
          .value("value", server.getOnlinePlayers().length)
          .flush();
        db.write("tps_value")
          .where("server", tag)
          .value("value", new BigDecimal(tps).setScale(2, 4).doubleValue())
          .flush();
        // Update.
        lastTime = now();
    }

    private long now() {
        return System.currentTimeMillis();
    }

    public void register() {
        if (registered) {
            throw new RuntimeException("Already registered!");
        }
        server.getScheduler().runTaskTimer(
                main,
                this,
                daily * 20,
                daily * 20);
        // Done.
        registered = true;
    }

}
