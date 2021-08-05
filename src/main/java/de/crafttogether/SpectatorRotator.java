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
import net.md_5.bungee.api.chat.TextComponent;

public class SpectatorRotator extends JavaPlugin {
    private static SpectatorRotator plugin;
    private Configuration config;

    public HashMap<Player, RotatorTask> spectating;
        
    public void onEnable() {
    	plugin = this;

        saveDefaultConfig();
        this.config = getConfig();

    	spectating = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        this.registerCommand("spectate", new CommandHandler());
    }
    
    public String getMessage(String messageKey) {
    	return ChatColor.translateAlternateColorCodes('&', this.config.getString(messageKey));
    }
    
    public void onDisable() {
    	for (Entry<Player, RotatorTask> entry: this.spectating.entrySet()) {
    		Player player = entry.getKey();
    		RotatorTask task = entry.getValue();
    		
    		task.cancel();
    		plugin.spectating.remove(player);
    		
    		if (player != null) {
    			if (player.getGameMode().equals(GameMode.SPECTATOR))
    				player.setSpectatorTarget(null);
    			
    			sendOutput(player, plugin.getMessage("RotatorDisabled"), 3);
    		}
    	}
    }
    
    public void registerCommand(String cmd, TabExecutor executor) {
    	this.getCommand(cmd).setExecutor(executor);
    	this.getCommand(cmd).setTabCompleter(executor);
    }
    
    public void sendOutput(Player p, String message, int delay) {
    	String displayMode = this.config.getString("DisplayMode");
    	int displayTimeout = this.config.getInt("DisplayTimeout");
		
		if (displayTimeout < delay && displayTimeout > 0)
			delay = displayTimeout;
		
		if (displayMode.equalsIgnoreCase("CHAT"))
			p.spigot().sendMessage(TextComponent.fromLegacyText(message));
		
		if (displayMode.equalsIgnoreCase("TITLE"))
			p.sendTitle("", message, 30, 20*delay, 30);
		
		if (displayMode.equalsIgnoreCase("ACTIONBAR"))
			ActionBar.sendActionBar(p, message, delay);
    }
    
	public void spectate(final Player player, final Player target, int duration, boolean clipped) {
		player.setGameMode(GameMode.SPECTATOR);
		player.setSpectatorTarget(null);
		player.teleport(target);
	
		Bukkit.getScheduler().runTaskLater(this, () -> {
			player.setGameMode(GameMode.SPECTATOR);
			sendOutput(player, getMessage("SpectatingTitle").replaceAll("%targetPlayer%", target.getName()), duration);

			if (clipped)
				player.setSpectatorTarget(target);
		}, 10L);
	}
    
    public static SpectatorRotator getInstance() {
        return plugin;
    }
}