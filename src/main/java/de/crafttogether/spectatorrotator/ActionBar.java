package de.crafttogether.spectatorrotator;

import java.util.HashMap;

import de.crafttogether.SpectatorRotatorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBar {
	public static HashMap<Player, Integer> tasks = new HashMap<>();
	
    public static void sendActionBar(Player p, String text, int seconds) {
        ActionBar.tasks.put(p, Bukkit.getScheduler().scheduleSyncRepeatingTask(SpectatorRotatorPlugin.instance(), new Runnable() {
            int time = 0;

            @Override
            public void run() {
                if(time == seconds && ActionBar.tasks.containsKey(p)){
                    Bukkit.getScheduler().cancelTask(ActionBar.tasks.get(p));
                }else{
                    time++;
    				p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text, ChatColor.WHITE));
                }

            }
        },0L, 20L));
    }
}
