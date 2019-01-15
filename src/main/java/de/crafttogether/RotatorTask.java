package de.crafttogether;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RotatorTask extends BukkitRunnable {
	private SpectatorRotator plugin;
	private ArrayList<Player> players;
	private Player player;
	private Player target;
	private int interval;
	private int index;
	public Boolean clipped;
	
	public RotatorTask(SpectatorRotator plugin, Player p, int interval, Boolean clipped) {
		this.plugin = plugin;
		this.players = new ArrayList<Player>();
		this.player = p;
		this.target = null;
		this.index = 0;
		this.interval = interval;
		this.clipped = clipped;
		
		this.runTaskTimer(plugin, 0, 20*interval);
	}

	public boolean isClipped() {
		return this.clipped;
	}
	
	public void run() {
		Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();

		for(Iterator<Player> it = this.players.listIterator(); it.hasNext();) {
			Player p = it.next();
			if (!onlinePlayers.contains(p) || !p.isOnline())
				it.remove();
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
			
			plugin.spectate(this.player, this.target, interval, this.clipped);
		}
		else {
			plugin.spectating.remove(this.player);
			this.player.sendMessage(plugin.getMessage("NoPlayerFound"));
			this.cancel();
		}
	}
}