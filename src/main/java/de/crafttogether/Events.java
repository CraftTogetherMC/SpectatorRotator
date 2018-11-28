package de.crafttogether;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Events implements Listener {
	private SpectatorRotator plugin;
	
	public Events(SpectatorRotator plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent ev) {
		Player p = ev.getPlayer();
		UUID uuid = p.getUniqueId();
		
		if (!plugin.spectating.containsKey(uuid))
			return;
		
		plugin.spectating.get(uuid).cancel();
		plugin.spectating.remove(uuid);
		p.setSpectatorTarget(null);

		p.sendTitle("", plugin.getMessage("RotatorDisabled"), -1, 60, -1);
	}
}
