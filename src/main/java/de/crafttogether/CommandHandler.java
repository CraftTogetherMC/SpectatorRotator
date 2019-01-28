package de.crafttogether;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
public class CommandHandler implements TabExecutor {
	private SpectatorRotator plugin;
	
	public CommandHandler() {
		this.plugin = SpectatorRotator.getInstance();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = null;
		RotatorTask task = null;
		Boolean clipped = true;
		int interval = 15;
		
		if (sender instanceof Player)
			p = (Player) sender;
		
		if (p == null || !cmd.getName().equalsIgnoreCase("spectate"))
			return true;

		if (!p.hasPermission("sr.spectate")) {
			p.sendMessage(plugin.getMessage("PermissionDenied"));
			return true;
		}
		
		if (args.length > 0) {
			Player target = null;
			target = Bukkit.getPlayer(args[0]);
			
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-noclip"))
					clipped = false;
			}
			
			if (target != null)
				plugin.spectate(p, target, 19, clipped);
			
			else if (args[0].matches("[0-9]+")) {
				interval = Integer.parseInt(args[0]);
				if (interval < 5 || interval > 300) {
					p.sendMessage(plugin.getMessage("InvalidArguments"));
				}
			}
		}

		if (plugin.spectating.containsKey(p)) {
			plugin.spectating.get(p).cancel();
			plugin.spectating.remove(p);
			
			if (p.getGameMode().equals(GameMode.SPECTATOR))
				p.setSpectatorTarget(null);

			p.sendTitle("", plugin.getMessage("RotatorDisabled"), -1, 60, -1);
		}
		else {				
			task = new RotatorTask(plugin, p, interval, clipped);
			plugin.spectating.put(p, task);
		}

		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		List<String> proposals = new ArrayList<String>();
		if (args.length < 2) {
			proposals.add("10");
			proposals.add("15");
			proposals.add("20");
			proposals.add("30");
		}
		proposals.add("-noclip");
		return proposals;
	}
}
