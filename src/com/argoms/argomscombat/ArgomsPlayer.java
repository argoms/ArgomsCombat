package com.argoms.argomscombat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ArgomsPlayer 
{
	//general stats:
	double stamina; //current stamina
	double staminaRegen; //stamina regen per 0.5 seconds
	double staminaMax; //maximum stamina
	double fatigue; //fatigue, acts as a reduction to staminaMax
	double staminaConsumed; //temporary value for stamina calculations
	double damageModifier; //damage modifier calculated by stamina
	double oldStamina; //used to decide whether or not to notify player of stamina
	
	//axe-specific:
	int comboCounter; //caps at 5
	boolean canCombo; //turns on when the combo window is open
	//sword-specific:
	boolean parrying;
	
	//internal stuff:
	ArgomsCombat mainPlugin;
	String damageType; 
	/*damage types:
	 * sword
	 * axe
	 */
	Player player; //the player entity whose stats these are
	public ArgomsPlayer(Player i1) //init stuff
	{
		comboCounter = 0;
		player = i1;
		stamina = 0;
		staminaRegen = 10;
		fatigue = 0;
		staminaMax = 100;
		damageModifier = 1;
		damageType = "";
		parrying = false;
	}
	
	//general
	public void Simulate()
	{
		//stamina regen related stuff:
		incrementStamina();
		if(stamina != oldStamina)
		{
			player.sendMessage("stamina: " + stamina + "/" + (staminaMax-fatigue));
		}
		oldStamina = stamina;
	}


	//melee combat:
	public void Swing(ItemStack item) //triggered when a player swings
	{
		staminaConsumed = 20; //default stamina consumed per swing
		//changing function based on held item:
		if(item != null)
		{
			if(isSword(item)) //sets relevant info if using a sword
			{
				staminaConsumed = 40;
				damageType = "sword";
			} else if(isAxe(item)) //and now if it's an axe
			{
				staminaConsumed = 40-(comboCounter*4);
				if(canCombo)
				{
					comboCounter += 1;
					player.sendMessage("combo " + comboCounter);

					canCombo = false;
				} else {
					comboCounter = 0;
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)ArgomsCombat.mainPlugin, new Runnable() {
				    public void run() {
				    	canCombo = true;
				    	player.sendMessage("combo window");
				    }
				}, 20L);
				Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)ArgomsCombat.mainPlugin, new Runnable() {
				    public void run() {
				    	if(canCombo)
				    	{
					    	canCombo = false;
					    	player.sendMessage("combo window ended");
				    	}
				    }
				}, 23L); //should be 23l, set to 40 for debug purposes
				damageType = "axe";
			}
		} else {
			//unarmed code goes here
		}
		//stamina consumption mechanics:
		damageModifier = ((double)stamina/(double)staminaConsumed);
		if(DecreaseStamina(staminaConsumed))
		{
			damageModifier = 1;
			//stamina -= staminaConsumed;
		}else {
			
			//Bukkit.broadcastMessage(((double)stamina/(double)staminaConsumed) + "_" + stamina + "_" + staminaConsumed);
		}
		/*
		if((stamina - staminaConsumed) >= 0) 
		{
			damageModifier = 1;
			stamina -= staminaConsumed;
		} else {
			damageModifier = ((double)stamina/(double)staminaConsumed); 
			//if stamina is less than full, damage is scaled based on the ratio of stamina available to ideal stamina consumed
			stamina = 0;
			if(fatigue+30 < staminaMax) //stamina never goes below 30
			{
				fatigue += 5;
			}
			
		}
		*/
		//player.sendMessage(damageModifier+""); //debug line
	}
	
	public void AltFire(ItemStack item) //alt fire (right click) abilities
	{
		staminaConsumed = 1;
		if(item != null && item.getType() != Material.BOW)
		{
			if(isSword(item))
			{
				staminaConsumed = 20;
				parrying = true;
		    	player.sendMessage("parry");
				Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)ArgomsCombat.mainPlugin, new Runnable() {
				    public void run() {
				    	parrying = false;
				    	player.sendMessage("no more parry" + parrying);
				    }
				}, 4L); //should be 4l, set to 200 for debug multiboxing purposes
			}
		}
		if(DecreaseStamina(staminaConsumed))
		{
		}else {
		}

	}
	
	public boolean DecreaseStamina(double change) //returns true if full, false if insufficient stamina for full usage
	{
		
		if((stamina - change) >= 0) 
		{
			stamina -= change;
			return true;
		} else {
			stamina = 0;
			if(fatigue+30 < staminaMax)
			{
				fatigue += 5;
			}
			return false;
			
		}
	}

	
	public double Hit(EntityDamageByEntityEvent event) //does damage calculations
	{	
		//Bukkit.broadcastMessage(""+damageModifier);
		Swing(player.getItemInHand());//since for whatever reason there's no player interaction event for punching someone in the face, this has to be manually called
		switch (damageType)
		{
		case "sword":
			break;
		case "axe": //axes do up to 100% more damage based on the combo counter, this damage ignores armor
			LivingEntity a = (LivingEntity) event.getEntity();
			a.damage((comboCounter/5)*damageModifier*(event.getDamage()));
			break;
		}
		return (event.getDamage())*damageModifier;

	}
	
	public void Parry(EntityDamageByEntityEvent event) //triggers when the player parries
	{
		DecreaseStamina(20);
	}
	
	public void Jump()
	{
		DecreaseStamina(5);
	}
	/*public double TakeDamage(double damage, String type)
	{
		double finalDamage = damage;
		if(parrying)
		{
			if(type == "melee")
			{
				finalDamage = 0;
			}
		}
		return finalDamage;
	}*/
	
	//mostly used by other functions within the class:
	private boolean isSword(ItemStack input) //checks if an item is a sword
	{
		if(input.getType() == Material.DIAMOND_SWORD || input.getType() == Material.GOLD_SWORD || input.getType() == Material.IRON_SWORD || input.getType() == Material.STONE_SWORD || input.getType() == Material.WOOD_SWORD)
		{
			return true;
		}else {
			return false;
		}
	}
	
	private boolean isAxe(ItemStack input) //checks if an item is an axe
	{
		if(input.getType() == Material.DIAMOND_AXE || input.getType() == Material.GOLD_AXE || input.getType() == Material.IRON_AXE || input.getType() == Material.STONE_AXE || input.getType() == Material.WOOD_AXE)
		{
			return true;
		}else {
			return false;
		}
	}
	
	public void incrementStamina() //natural stamina regeneration
	{
		//actual regen:
		if(!player.isSprinting()) //stamina only regenerates if the player is not sprinting
		{
			stamina += staminaRegen; 
		}
		if(stamina > staminaMax-fatigue)
		{
			stamina = staminaMax-fatigue;
		}
		//heads up to player:
	}
}
