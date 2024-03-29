package de.crafttogether.spectatorrotator;

import java.util.ArrayList;
import java.util.List;

import de.crafttogether.SpectatorRotatorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class CommandHandler implements TabExecutor {
	private final SpectatorRotatorPlugin plugin;
	
	public CommandHandler() {
		this.plugin = SpectatorRotatorPlugin.instance();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		RotatorTask task;
		Player p = null;
		boolean clipped = true;
		int interval = 15;
		
		if (sender instanceof Player)
			p = (Player) sender;
		
		if (p == null || (!cmd.getName().equalsIgnoreCase("spectate")
			&& !cmd.getName().equalsIgnoreCase("spec")
			&& !cmd.getName().equalsIgnoreCase("rspec")
			&& !cmd.getName().equalsIgnoreCase("rspectate"))
		)
			return true;

		if (!p.hasPermission("sr.spectate")) {
			p.sendMessage(plugin.getMessage("PermissionDenied"));
			return true;
		}

		if (args[0].equalsIgnoreCase("reload")) {
			plugin.reload();
			p.sendMessage(plugin.getMessage("ConfigReloaded"));
			return true;
		}

		Player target = Bukkit.getPlayer(args[0]);

		for (String arg : args) {
			if (arg.equals("-noclip")) {
				clipped = false;
				break;
			}
		}

		if (target != null)
			plugin.spectate(p, target, 19, clipped);

		else if (args[0].matches("[0-9]+")) {
			interval = Integer.parseInt(args[0]);
			if (interval < 5 || interval > 300) {
				p.sendMessage(plugin.getMessage("InvalidArguments"));
			}
		}

		if (plugin.spectating.containsKey(p)) {
			plugin.spectating.get(p).cancel();
			plugin.spectating.remove(p);
			
			if (p.getGameMode().equals(GameMode.SPECTATOR))
				p.setSpectatorTarget(null);

			plugin.sendOutput(p, plugin.getMessage("RotatorDisabled"), 3);
		}
		else {				
			task = new RotatorTask(plugin, p, interval, clipped);
			plugin.spectating.put(p, task);
		}

		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		List<String> proposals = new ArrayList<>();
		if (args.length < 2) {
			proposals.add("10");
			proposals.add("15");
			proposals.add("20");
			proposals.add("30");
			proposals.add("reload");
		}
		proposals.add("-noclip");
		return proposals;
	}
}
