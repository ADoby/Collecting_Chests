package com.gmail.zimmerlint.plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener {
	
	Player player;
	
	public PlayerListener(Player player){
		this.player = player;
	}
	
	public int OnPlayerInteract(PlayerInteractEvent event){
		return 0;
	}
	
	public Player getPlayer() {
		return player;
	}

}
