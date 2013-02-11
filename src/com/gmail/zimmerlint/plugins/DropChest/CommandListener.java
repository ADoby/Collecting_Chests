package com.gmail.zimmerlint.plugins.DropChest;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class CommandListener {
	
	Player player;
	
	public CommandListener(Player player){
		this.player = player;
	}
	
	public boolean OnPlayerInteract(PlayerInteractEvent event){
		return false;
	}
}
