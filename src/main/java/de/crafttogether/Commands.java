package de.crafttogether;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Commands implements TabExecutor {
	private SpectatorRotator plugin;
	
	public Commands(SpectatorRotator plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		BukkitTask task = null;
		int interval = 15;
		
		if (sender instanceof Player)
			p = (Player) sender;
		
		if (p == null || !cmd.getName().equalsIgnoreCase("spectate"))
			return true;

		if (!p.hasPermission("sr.spectate")) {
			p.sendMessage(plugin.getMessage("PermissionDenied"));
			return true;
		}
		
		if (args.length > 0 && args[0].matches("[0-9]+")) {
			interval = Integer.parseInt(args[0]);
			if (interval < 5 || interval > 300) {
				p.sendMessage(plugin.getMessage("InvalidArguments"));
			}
		}

		if (plugin.spectating.containsKey(p)) {
			plugin.spectating.get(p).cancel();
			plugin.spectating.remove(p);
			plugin.targets.remove(p);
			
			if (p.getGameMode().equals(GameMode.SPECTATOR))
				p.setSpectatorTarget(null);

			p.sendTitle("", plugin.getMessage("RotatorDisabled"), -1, 60, -1);
		}
		else {				
			task = new RotatorTask(plugin, p, interval).runTaskTimer(plugin, 0, 20*interval);
			plugin.spectating.put(p, task);
		}

		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}
}
