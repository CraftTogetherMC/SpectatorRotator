package de.crafttogether;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class SpectatorRotator extends JavaPlugin {
    private static SpectatorRotator plugin;
    private Configuration config;

    public HashMap<Player, RotatorTask> spectating;
        
    public void onEnable() {
    	plugin = this;

        saveDefaultConfig();
        this.config = getConfig();

    	spectating = new HashMap<Player, RotatorTask>();

        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        this.registerCommand("spectate", new CommandHandler());
    }
    
    public String getMessage(String messageKey) {
    	return ChatColor.translateAlternateColorCodes('&', this.config.getString(messageKey));
    }
    
    public void onDisable() {
    	for (Entry<Player, RotatorTask> entry: this.spectating.entrySet()) {
    		Player p = entry.getKey();
    		RotatorTask task = entry.getValue();
    		
    		task.cancel();
    		plugin.spectating.remove(p);
    		
    		if (p != null) {
    			if (p.getGameMode().equals(GameMode.SPECTATOR))
    				p.setSpectatorTarget(null);
    			
    			p.sendTitle("", plugin.getMessage("RotatorDisabled"), -1, 60, -1);
    		}
    	}
    }
    
    public void registerCommand(String cmd, TabExecutor executor) {
    	this.getCommand(cmd).setExecutor(executor);
    	this.getCommand(cmd).setTabCompleter(executor);
    }
    
	public void spectate(final Player player, final Player target, int titleDelay, boolean clipped) {
		player.setGameMode(GameMode.SPECTATOR);
		player.setSpectatorTarget(null);
		player.teleport(target);
	
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				player.setGameMode(GameMode.SPECTATOR);
				player.sendTitle("", getMessage("SpectatingTitle").replaceAll("%targetPlayer%", target.getName()), 30, 20*titleDelay, 30);
				
				if (clipped)
					player.setSpectatorTarget(target);
			}
		}, 10L);
	}
    
    public static SpectatorRotator getInstance() {
        return plugin;
    }
}