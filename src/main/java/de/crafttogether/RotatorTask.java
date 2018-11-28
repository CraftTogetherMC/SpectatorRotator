package de.crafttogether;

import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RotatorTask extends BukkitRunnable {
	private SpectatorRotator plugin;
	private ArrayList<Player> players;
	private Player player;
	private Player target;
	private int interval;
	private int index;
	
	public RotatorTask(SpectatorRotator plugin, Player p, int interval) {
		this.plugin = plugin;
		this.players = new ArrayList<Player>();
		this.player = p;
		this.target = null;
		this.index = 0;
		this.interval = interval;
	}

	public void run() {
		Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();

		ArrayList<Player> newList = this.players;
		for (Player p : newList) {
			if (!onlinePlayers.contains(p) || !p.isOnline())
				this.players.remove(p);
		}
		
		for (Player p : onlinePlayers) {
			if (this.players.contains(p) || !p.isOnline())
				continue;
			
			if (p.getName().equals(this.player.getName()))
				continue;
			
			if (p.hasPermission("sr.spectate.bypass"))
				continue;
			
			this.players.add(p);
		}

		for (int i = 0; i < this.players.size(); i++) {
			if (this.index > this.players.size() -1)
				this.index = 0;
			
			if (this.index == i && !this.players.get(index).equals(this.target)) {				
				this.target = this.players.get(index);
				this.index++;
				break;
			}
		}
		
		if (this.target != null) {
			if (this.player.getSpectatorTarget() != null && this.player.getSpectatorTarget().equals(this.target)) {
				this.player.sendTitle("", plugin.getMessage("SpectatingTitle").replaceAll("%targetPlayer%", target.getName()), -1, 20*interval, -1);
				return;
			}
			
			this.player.setGameMode(GameMode.SPECTATOR);
			this.player.setSpectatorTarget(null);
			this.player.teleport(this.target);
			
			final Player p = this.player;
			final Player target = this.target;
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					p.setGameMode(GameMode.SPECTATOR);
					p.sendTitle("", plugin.getMessage("SpectatingTitle").replaceAll("%targetPlayer%", target.getName()), 30, 20*interval, 30);
					p.setSpectatorTarget(target);
				}
			}, 10L);
		}
		else {
			plugin.spectating.remove(this.player);
			this.player.sendMessage(plugin.getMessage("NoPlayerFound"));
			this.cancel();
		}
	}
}