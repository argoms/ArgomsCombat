package com.argoms.argomscombat;

//import java.util.ArrayList;
//import java.util.List;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
/*import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;*/

public class ArgomsPlayers 
{
	//Scoreboard board;
	//ScoreboardManager manager;
	int timer; //constantly increases, useful for checking time
	//public static List<ArgomsPlayer> playerList; //used to as3 not sure if this should be declared somewhere else actually im scared hold me
	public static HashMap<Player, ArgomsPlayer>playerList = new HashMap<>(); //ok it turns out hashmaps are better, are they even a thing in as3?
	public ArgomsPlayers()
	{
		playerList = new HashMap<>();
		//playerList = new ArrayList<ArgomsPlayer>(); //clears the list, potential problems if refreshing plugin with players online?
		timer = 0;
		//manager = Bukkit.getScoreboardManager();
	    //board = manager.getNewScoreboard();

	}
	
	public void AddPlayer(Player target)
	{

		//
		ArgomsPlayer obj = new ArgomsPlayer(target);
		playerList.put(target, obj);
		//playerList.add(obj);
		
		//hud stuff:
		/*
		Scoreboard board = manager.getNewScoreboard();
		Team tempTeam = board.registerNewTeam(target.getName());
		tempTeam.addPlayer(target);
		Objective objective1 = board.registerNewObjective("string", "criteria");
		objective1.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		Score stamina = objective1.getScore("stamina:" + obj.stamina + "/" + (obj.staminaMax-obj.fatigue));
		stamina.setScore(0);
		target.setScoreboard(board);
		Bukkit.broadcastMessage("potato");
		*/
	}
	public void RemovePlayer(Player target)
	{
		playerList.remove(target);
		//playerList.remove(FindArgomsPlayer(target));
		//playerList.remove(target);
		//target.getName()
	}
	public ArgomsPlayer FindArgomsPlayer(Player target) //finds the corresponding plugin player object for a player, and yes i realized
	{
		return playerList.get(target);
	}
	public void Run() //applies effects that happen every 0.5 seconds
	{
		//Bukkit.broadcastMessage("1"+ playerList.size());
		timer++;
		if(playerList.size()>0)
		{
			for(ArgomsPlayer thing: playerList.values())
			{
				thing.Simulate();
			}
		}
		
		
	}

}
