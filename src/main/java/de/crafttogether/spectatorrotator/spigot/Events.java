package de.crafttogether.spectatorrotator.spigot;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Events implements Listener {
private SpectatorRotator plugin;

	public Events(SpectatorRotator plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerToggleSneak(PlayerToggleSneakEvent ev) {
		Player p = ev.getPlayer();
		
		if (!plugin.spectating.containsKey(p) || !plugin.spectating.get(p).isClipped())
			return;
		
		plugin.spectating.get(p).cancel();
		plugin.spectating.remove(p);
		
		if (p.getGameMode().equals(GameMode.SPECTATOR))
			p.setSpectatorTarget(null);
		
		p.sendTitle("", plugin.getMessage("RotatorDisabled"), -1, 60, -1);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent ev) {
		Player p = ev.getPlayer();
		Player spectator = null;
		Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
		
		for (Player onlinePlayer : onlinePlayers) {
			if (onlinePlayer.getSpectatorTarget() != null && onlinePlayer.getSpectatorTarget().equals(p)) {
				spectator = onlinePlayer;
				break;
			}
		}
		
		if (spectator == null)
			return;
		
		if (spectator.getGameMode().equals(GameMode.SPECTATOR))
			spectator.setSpectatorTarget(null);

		Player finalSpectator = spectator;
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				finalSpectator.teleport(p);
				finalSpectator.setGameMode(GameMode.SPECTATOR);
				finalSpectator.setSpectatorTarget(p);
			}
		}, 40L);
	}
}
