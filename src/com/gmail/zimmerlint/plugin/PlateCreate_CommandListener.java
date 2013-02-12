package com.gmail.zimmerlint.plugin;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlateCreate_CommandListener extends PlayerListener{

	private Block plate = null;
	private Chest chest = null;
	
	public PlateCreate_CommandListener(Player player){
		super(player);
	}	
	
	public Block getPlate(){
		return plate;
	}
	
	public Chest getChest(){
		return chest;
	}

	public int OnPlayerInteract(PlayerInteractEvent event){
		if(plate==null && event.getClickedBlock().getTypeId() == 72 && event.getPlayer() == player) {
			plate = event.getClickedBlock();
			//Block should not be destroyed
			event.setCancelled(true);
			
			if(chest==null){
				player.sendMessage("§eCollecting Chests:");
				player.sendMessage("  §aPlate saved, click on chest next");
			}else{
				player.sendMessage("§eCollecting Chests:");
				player.sendMessage("  §aPlate saved");
				return 1;
			}
		}else if(chest==null && event.getClickedBlock().getTypeId() == 54 && event.getPlayer() == player) {
			chest = (Chest)event.getClickedBlock().getState();
			//Block should not be destroyed
			event.setCancelled(true);
			
			if(plate==null){
				player.sendMessage("§eCollecting Chests:");
				player.sendMessage("  §bChest saved, click or step on plate next");
			}else{
				player.sendMessage("§eCollecting Chests:");
				player.sendMessage("  §aChest saved");
				return 1;
			}
		}else{
			if(!event.getClickedBlock().equals(plate)){
				//Wrong block clicked, we should abort command
				player.sendMessage("§eCollecting Chests:");
				player.sendMessage("  §cWrong Block, connection creation aborted");
				return 2;
			}
		}
		//Command not finished yet
		return 0;
	}
}
