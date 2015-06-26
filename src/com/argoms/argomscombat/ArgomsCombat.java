package com.argoms.argomscombat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.entity.*;

public class ArgomsCombat extends JavaPlugin
{
	public static ArgomsPlayers playerSim;
	public static ArgomsCombat mainPlugin;
	public ArgomsCombat()
	{
		mainPlugin = this;
	}
	@Override
	public void onDisable()
	{
		this.saveConfig();
	}
	
	@Override
	public void onEnable()
	{
		Bukkit.broadcastMessage("sim");
		getServer().getPluginManager().registerEvents(new ArgomsCombatListener(), this);
		this.saveConfig();

		//player mechanics simulation
		playerSim = new ArgomsPlayers();
		for(int i = 0; i < Bukkit.getOnlinePlayers().size(); i++)
		{
			
			playerSim.AddPlayer((Player)(Bukkit.getOnlinePlayers().toArray()[i]));
		}
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	playerSim.Run();
            	
            }
        }, 0L, 10L);
	}
	
	
	
	public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args)
	{
		if(commandLabel.equalsIgnoreCase("test"))
		{
			commandSender.sendMessage("it works woo");
			if(args[0].equals("get"))
			{
				commandSender.sendMessage(ChatColor.YELLOW + "pizza" + this.getConfig().getString("test"));
			}
			if (args[0].equals("set") && args[1] != null)
			{
				this.getConfig().set("test", args[1]);
				commandSender.sendMessage("var set to "+ args[1]);
				this.saveConfig();
			}
			return true;
		}
		return false;
	}
}
