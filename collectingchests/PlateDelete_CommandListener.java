package com.gmail.zimmerlint.plugin;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlateDelete_CommandListener extends PlayerListener {

	Block plate;
	
	public PlateDelete_CommandListener(Player player) {
		super(player);
	}
	
	@Override
	public int onPlayerInteract(PlayerInteractEvent event){
		if(event.getClickedBlock().getTypeId() == 72 && event.getPlayer() == player) {
			plate = event.getClickedBlock();
			event.setCancelled(true);
			return 1; //finished
		}else{
			//error/wrong block
			return 2;
		}
	}

	public Block getPlate() {
		return plate;
	}

}
