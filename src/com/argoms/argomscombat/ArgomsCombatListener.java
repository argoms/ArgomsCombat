package com.argoms.argomscombat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ArgomsCombatListener implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		//Bukkit.broadcastMessage("A player has joined you fucks");
		ArgomsCombat.playerSim.AddPlayer(event.getPlayer());
		
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		ArgomsCombat.playerSim.RemovePlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		//stamina is consumed even if you miss bc of this
		if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) 
		{
			//ArgomsCombat.playerSim.FindArgomsPlayer(event.getPlayer()).Swing(event.getItem());
			FAP(event.getPlayer()).Swing(event.getItem());
		} else if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			//ArgomsCombat.playerSim.FindArgomsPlayer(event.getPlayer()).AltFire(event.getItem());
			FAP(event.getPlayer()).AltFire(event.getItem());
		}
	}
	
	@EventHandler
    public void onEnttiyDamageByEntity(EntityDamageByEntityEvent event) 
	{
		//used to modify damage (most common reason is insufficient stamina)
        if (event.getDamager() instanceof Player) 
        {
        	ArgomsPlayer setPlayer = FAP((Player)event.getEntity());//ArgomsCombat.playerSim.FindArgomsPlayer((Player)event.getEntity());
        	Player player = (Player)event.getDamager();
        	if(setPlayer.parrying)
        	{
        		event.setDamage(0);
        		setPlayer.Parry(event);
        		Bukkit.broadcastMessage("parry");
        	} else {
        		if(player.isBlocking())
        		{
        			setPlayer.DecreaseStamina(20);
        			event.setDamage(setPlayer.Hit(event)*0.5);
        			Bukkit.broadcastMessage("block hit");
        		} else {
        			event.setDamage(setPlayer.Hit(event));
        			Bukkit.broadcastMessage("regular hit");
        		}
        	}
        }
    }
	
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event)
	{
		ArgomsCombat.playerSim.FindArgomsPlayer(event.getPlayer()).fatigue = 0; //entering a bed removes fatigue
	}
	
	@EventHandler
	public void onPlayerJump(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		if(event.getFrom().getY() + 1 == event.getTo().getY() || event.getFrom().getY() + 2 == event.getTo().getY())
		{ 
			FAP(player).Jump();
		}
	}
	
	public ArgomsPlayer FAP(Player player) //quicker than typing out the whole argomscombat heirarchy  //shut up
	{
		return ArgomsCombat.playerSim.FindArgomsPlayer(player);
	}
}
