package de.crafttogether;

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
		
		if (!plugin.spectating.containsKey(p))
			return;
		
		plugin.spectating.get(p).cancel();
		plugin.spectating.remove(p);
		plugin.targets.remove(p);
		
		if (p.getGameMode().equals(GameMode.SPECTATOR))
			p.setSpectatorTarget(null);
		
		p.sendTitle("", plugin.getMessage("RotatorDisabled"), -1, 60, -1);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent ev) {
		Player p = ev.getPlayer();
		
		if (!plugin.targets.containsKey(p))
			return;

		Player spectator = plugin.targets.get(p);
		
		if (spectator == null || !spectator.isOnline())
			return;

		System.out.println("spectator REALLY found " + spectator.getName());
		spectator.sendMessage(p.getName() + " is teleporting");
		
		if (spectator.getGameMode().equals(GameMode.SPECTATOR))
			spectator.setSpectatorTarget(null);

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				spectator.teleport(p);
				spectator.setGameMode(GameMode.SPECTATOR);
				spectator.setSpectatorTarget(p);
			}
		}, 60L);
	}
}
