package de.crafttogether;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatColor;

public class SpectatorRotator extends JavaPlugin {
    private static SpectatorRotator plugin;
    private Configuration config;

    public HashMap<UUID, BukkitTask> spectating;
        
    public void onEnable() {
    	plugin = this;

        saveDefaultConfig();
        this.config = getConfig();
    	
    	spectating = new HashMap<UUID, BukkitTask>();

        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        this.registerCommand("spectate", new Commands(this));
    }
    
    public String getMessage(String messageKey) {
    	return ChatColor.translateAlternateColorCodes('&', this.config.getString(messageKey));
    }
    
    public void onDisable() {
    	for (Entry<UUID, BukkitTask> entry: this.spectating.entrySet()) {
    		UUID uuid = entry.getKey();
    		Player p = Bukkit.getServer().getPlayer(uuid);
    		BukkitTask task = entry.getValue();
    		
    		task.cancel();
    		plugin.spectating.remove(uuid);
    		
    		if (p != null) {
    			p.setSpectatorTarget(null);
    			p.sendTitle("", plugin.getMessage("RotatorDisabled"), -1, 60, -1);
    		}
    	}
    }
    
    public void registerCommand(String cmd, TabExecutor executor) {
    	this.getCommand(cmd).setExecutor(executor);
    	this.getCommand(cmd).setTabCompleter(executor);
    }
    
    public static SpectatorRotator getInstance() {
        return plugin;
    }
}