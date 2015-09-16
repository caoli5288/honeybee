package com.mengcraft.honeybee;

import org.bukkit.Server;

import com.mengcraft.influxdb.InfluxHandler;

public class Collector implements Runnable {

    private Main main;
    private Server server;
    private InfluxHandler source;
    private String tag;

    private int daily;
	private long lastTime;

    public void bind(Main main, InfluxHandler source) {
		if (getMain() != main) {
			setSource(source);
			setTag(main.getConfig().getString("collect.tag"));
			setDaily(main.getConfig().getInt("collect.interval"));
			// Setup task.
			getServer().getScheduler().runTaskTimer(main, this,
					    getDaily() * 20, getDaily() * 20);
			setMain(main);
			setLastTime(now());
			setServer(main.getServer());
		}
	}

	public void run() {
		// Record.
		source.write("player_value")
			  .where("server", tag)
			  .value("value", server.getOnlinePlayers().length)
			  .flush();
		source.write("tps_value")
			  .where("server", tag)
			  .value("value", (getDaily() * 20000) / a())
			  .flush();
		// Update.
		lastTime = now();
	}
	
    private double a() {
		return now() - getLastTime();
	}

	private long now() {
        return System.currentTimeMillis();
    }

	private Main getMain() {
		return main;
	}

	private void setMain(Main main) {
		this.main = main;
	}

	private Server getServer() {
		return server;
	}

	private void setServer(Server server) {
		this.server = server;
	}

	private String getTag() {
		return tag;
	}

	private void setTag(String tag) {
		this.tag = tag;
	}

	private int getDaily() {
		return daily;
	}

	private void setDaily(int daily) {
		this.daily = daily;
	}

	private long getLastTime() {
		return lastTime;
	}

	private void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	private InfluxHandler getSource() {
		return source;
	}

	private void setSource(InfluxHandler source) {
		this.source = source;
	}

}
